(ns prose.core
  (:require [clojure.string :as string] 
            [clojure.pprint :as pprint]
            [instaparse.core :as insta]))

(def parser (insta/parser (clojure.java.io/resource "grammar.bnf")))

(defn parse [path]
  (insta/parse parser (slurp (clojure.java.io/file path))))

(defn pretty [path]
  (pprint/pprint (parse path)))

(declare gen)

(defn compile-file [path path-out]
  (let [res (parse path)]
    (if (vector? res)
      (spit (clojure.java.io/file path-out) (gen "" res))
      (println res))))

(def newlines (atom []))
(def indent (atom 0))

(defn str* [arg]
  (apply str arg))

(defn ->call [sym & args]
  (concat [:call [:symbol sym]] (apply concat args)))

(defn join [out args & [left right]]
  (let [res (string/join " " (map #(gen "" %) args))
        whitespace (re-find #"\s+$" res)] 
    (str out (or left "") (string/trimr res) (or right "") whitespace)))

(defn keep-indexed* [tail pred key] 
  (keep-indexed (fn [idx item] (if (pred key (first item)) idx)) tail))

(defn program [out [_ & [head & _ :as tail]]]
  (str* (concat [out] (map #(gen "" %) (if (= :do (first head)) (rest head) tail)))))

(defn do-node [out [_ & tail]]
  (gen out (->call "do" tail)))

(defn n [out _]
  (swap! newlines #(conj % "\n"))
  out)

(defn fn-node [start out tail] 
  (let [tail (vec tail)
        keep-indexed* (partial keep-indexed* tail)
        body-idx (last (keep-indexed* not= :n))
        arg-idxs (drop start (keep-indexed* = :symbol))
        arg-idxs (if (= body-idx (last arg-idxs)) (butlast arg-idxs) arg-idxs) 
        larg (if (seq arg-idxs) (last arg-idxs) start)
        subvec* #(subvec tail % %2)
        args (subvec* (or (first arg-idxs) start) (inc larg))
        karg-idxs (keep-indexed* = :pair)
        karg-idxs (if (= body-idx (last karg-idxs)) (butlast karg-idxs) karg-idxs) 
        fkarg (or (first karg-idxs) (inc larg))
        lkarg (if (seq karg-idxs) (inc (last karg-idxs)) fkarg)
        kargs (subvec* fkarg lkarg)
        keys (if (seq kargs) 
               (concat 
                [[:symbol "&"] [:keyword ":keys"] 
                 (cons :vector (keep #(if (= :pair (first %)) (second %)) kargs))
                 [:keyword ":or"] (cons :map* kargs)])
               kargs)
        params (cons :vector (concat args keys))
        [body-h & body-t :as body] (nth tail body-idx)
        body (if (= :do body-h) body-t [body])]  
    (join out 
          (concat (subvec* 0 (if (= 1 larg) larg (dec larg))) 
                  [params] 
                  (subvec tail lkarg body-idx) 
                  body 
                  (subvec tail (inc body-idx)))
          "(" ")")))

(defn let-node [out [head & _ :as tail]] 
  (let [tail (vec tail)
        idx (last (keep-indexed* tail not= :n))
        kargs (cons :vector (subvec tail 1 idx))
        [body-h & body-t :as body] (nth tail idx)
        body (if (= :do body-h) body-t [body])]  
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
       (cond 
         (= [:space] op) (if (= :call (first right)) 
                           (concat (subvec right 0 2) [left] (subvec right 2))
                           [:call right left])
         (= "==" (str* (rest op))) (->call "=" [left right])
         :else [:call op left right])))

(defn assignment [out [_ & [left right]]]
  (gen out  
       (if (= (first right) :call)
         (concat [:call (second right) left] (subvec right 2))  
         (->call "def" [left right]))))

(defn list-node [out [_ & tail]] 
  (gen out (->call "list" tail)))

(defn map-node [out [_ & tail]] 
  (join out 
        (map 
         #(if (= (get-in % [1 0]) :symbol) 
            (update-in % [1] (fn [node] (vector :keyword ":" (last node)))) 
            %) 
         tail)
        "{" "}"))

(defn map-node* [out [_ & tail]] 
  (join out tail "{" "}"))

(defn pair [out [_ & tail]] 
  (join out tail))

(defn set-node [out [_ & tail]] 
  (join out tail "#{" "}"))

(defn vector-node [out [_ & tail]] 
  (join out tail "[" "]"))

(defn group [out [_ exp]] 
  (gen out exp))

(defn comment-node [out [_ & tail]] 
  (str out " " (str* tail)))

(defn default [out [_ & tail]] 
  (str out (str* tail)))

(defn shift [node-fn]
  (fn [& args]
    (swap! indent inc)
    (let [res (apply node-fn args)]
      (swap! indent dec)
      res)))

(defn gen [out [head & tail :as node]]
  (let [node-fn (case head 
                  :program program
                  :do do-node
                  :n n 
                  :call (shift call)
                  :operation operation
                  :assignment assignment 
                  :list (shift list-node)
                  :map (shift map-node)
                  :map* (shift map-node*)
                  :pair pair 
                  :set (shift set-node)
                  :vector (shift vector-node)
                  :group group 
                  :comment comment-node
                  default)
        res (str* (concat [out] (map #(str % (string/join (repeat (* @indent 2) \space))) @newlines)))]
    (reset! newlines [])
    (node-fn res node)))


