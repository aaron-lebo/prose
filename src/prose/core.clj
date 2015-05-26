(ns prose.core
  (:require [clojure.string :as string] 
            [clojure.pprint :as pprint]
            [cljfmt.core :as cljfmt]
            [instaparse.core :as insta]))

(declare gen)
(def indent (atom 0))
(def newlines (atom []))

(defn str+ [out f sequence]
  (str out (string/join (map f sequence))))

(defn node? [head node]
  (= head (first node)))

(defn ->call [sym & args]
  (concat [:call [:symbol sym]] (apply concat args)))

(defn join [out args & [left right]]
  (let [res (string/join " " (map (partial gen "") args))
        regex #"\s+;;.*$"
        match (re-find regex res)]
    (str out left (string/replace res regex "") right match)))

(defn keep-indexed* [tail ? key] 
  (keep-indexed (fn [idx [f-itm & _]] (if (? key f-itm) idx)) tail))

(defn program [out [_ & [[ff-tail & rf-tail] & _ :as tail]]]
  (str+ out (partial gen "") (if (= :do ff-tail) rf-tail tail)))

(defn do-node [out [_ & [f-tail & r-tail :as tail]]]
  (gen out (->call "do" (if (node? :n f-tail) r-tail tail))))

(defn n [out _]
  (swap! newlines #(conj % "\n"))
  out)

(defn fn-node [start out tail] 
  (let [tail (vec tail)
        keep-indexed* (partial keep-indexed* tail)
        body-idx (last (keep-indexed* not= :n))
        arg-idxs (drop start (keep-indexed* = :symbol))
        arg-idxs (if (= body-idx (last arg-idxs)) (butlast arg-idxs) arg-idxs) 
        subvec* (partial subvec tail)
        args (if (seq arg-idxs) (subvec* (first arg-idxs) (inc (last arg-idxs))) [])
        karg-idxs (keep-indexed* = :pair)
        karg-idxs (if (= body-idx (last karg-idxs)) (butlast karg-idxs) karg-idxs) 
        kargs (if (seq karg-idxs) (subvec* (first karg-idxs) (inc (last karg-idxs))) [])
        kargs (map #(if (node? :pair %) (assoc-in % [0] :symbol-pair) %) kargs)
        keys (if (seq kargs) 
               (concat 
                [[:symbol "&"] [:keyword ":keys"] 
                 (cons :vector (keep #(if (node? :symbol-pair %) (second %)) kargs))
                 [:keyword ":or"] (cons :map kargs)])
               kargs)
        params (cons :vector (concat args keys))
        [_ & r-body :as body] (nth tail body-idx)
        body (if (node? :do body) r-body [body])]  
    (join out 
          (concat (subvec* 0 (or (first arg-idxs) (first karg-idxs) body-idx)) 
                  [params] 
                  (subvec* (inc (or (last karg-idxs) (last arg-idxs) (dec body-idx))) body-idx) 
                  body 
                  (subvec tail (inc body-idx)))
          "(" ")")))

(defn let-node [out [head & _ :as tail]] 
  (let [tail (vec tail)
        idx (last (keep-indexed* tail not= :n))
        kargs (cons :vector 
                    (map #(if (node? :pair %) (assoc-in % [0] :symbol-pair) %)
                         (subvec tail 1 idx)))
        [_ & r-body :as body] (nth tail idx)
        body (if (node? :do body) r-body [body])]  
    (join out (concat [head kargs] body (subvec tail (inc idx))) "(" ")")))

(defn call [out [_ & [head _ :as tail]]] 
  (case head
    [:symbol "fn"] (fn-node 1 out tail)
    [:symbol "defn"] (fn-node 2 out tail)
    [:symbol "defmacro"] (fn-node 2 out tail)
    [:symbol "let"] (let-node out tail)
    (join out tail "(" ")")))

(defn operation [out [_ left op right]] 
  (gen out 
       (case op 
         [:space] (if (node? :call right)
                    (concat (subvec right 0 2) [left] (subvec right 2))
                    [:call right left])
         [:operator "=" "="] (->call "=" [left right])
         [:call op left right])))

(defn assignment [out [_ & [left right]]]
  (gen out  
       (if (node? :call right)
         (concat [:call (second right) left] (subvec right 2))  
         (->call "def" [left right]))))

(defn list-node [out [_ & tail]] 
  (join out tail "(" ")"))

(defn map-node [out [_ & tail]] 
  (join out tail "{" "}"))

(defn pair [out [_ & tail]] 
  (join out tail))

(defn pair! [out [_ & [[_ s-left :as left] right :as tail]]] 
  (join out 
        (if (node? :symbol left)
          [[:keyword ":" s-left] right]
          tail)))

(defn set-node [out [_ & tail]] 
  (join out tail "#{" "}"))

(defn vector-node [out [_ & tail]] 
  (join out tail "[" "]"))

(defn group [out [_ exp]] 
  (gen out exp))

(defn default [out [_ & tail]] 
  (str out (string/join tail)))

(defn gen [out [head & tail :as node]]
  (let [node-fn (case head 
                  :program program
                  :do do-node
                  :n n 
                  :call call
                  :operation operation
                  :assignment assignment 
                  :list list-node
                  :map map-node
                  :pair pair!
                  :symbol-pair pair
                  :set set-node
                  :vector vector-node
                  :group group 
                  default)
        res (str+ out identity @newlines)]
    (reset! newlines [])
    (node-fn res node)))

(def parser (insta/parser (clojure.java.io/resource "grammar.bnf")))

(defn parse [path]
  (insta/parse parser (slurp (clojure.java.io/file path))))

(defn pretty [path]
  (pprint/pprint (parse path)))

(defn compile-file [path path-out]
  (let [res (parse path)]
    (if (vector? res)
      (spit (clojure.java.io/file path-out) (cljfmt/reformat-string (gen "" res)))
      (println res))))
