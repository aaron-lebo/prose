;; a comment (note: two semicolons and not one unlike Clojure)

;; literals (integer, float, string, regex, keyword, vector, set, list)

1
1.0
"string"
#"rege"
:keyword
[1 2 3]
#{:a :b :c}
(1 2 3)
();; empty list
(1);; list with one item
1;; the expression 1

;; hash-maps and pairs

{:a 1 :b 2 :c 3};; hash-map
:a 1
0 1  ;; becomes 0 1;; becomes 0 1
"hello" "world"  ;; becomes "hello" "world";; becomes "hello" "world"

;; this behavior is mostly to allow for nice-looking hash maps, default args, etc

;; symbols - like Clojure,a wide variety of identifiers not available in other languages

awesome?
awesome!
so-good
<alright>

;; calls

(println 1)
(println 1 2 3)
(if true :yes :no)
(my-function "test" :test "this");; keyword args, when applicable

;; application - functions can be applied in a way that looks like method invocation or UNIX pipes 

(println 1)
(println 1 2 3)
(if true :yes :no)
(my-function "test" :test "this")
(println (dec (inc 1)));; can also be chained

;; newlines - denote multiple expressions, much like Ruby (you can also use semicolons)
;; multiple expressions get wrapped in a Clojure do form except where that is uncessary (let, defn, etc bodies).

(if true :yes  
  (do (println :no)  
    :no)) 

(if true :yes (do (println :no) :no))

;; operators - symbols which contain only non-alphanumeric characters (currently, any combination of .!$%&*_+=|<>?-)

+
-
*
/
?
!!!
<|>

;; operations - space is imporant 

;; 1+1 is a syntax error, not an operation
(+ 1 1);; this is an operation

;; space between args and operators allows operators to act like symbols in many contexts

(def sum (partial reduce +))
(+ 1 2)

;; with a single exception (as we'll see), there's no precedence

(* (+ 1 1) 2);; = 4

;; lack of precedence keeps both the implementation and usage simple 
;; use parens to dictate flow

(+ 1 (* 1 2));; = 3 

;; assignment - lower precedence than all other operations 
;; translates to (def left right) for all right-hand expressions except for calls 

(def a 1)
(def b [1 2 3])
(def c (partial reduce +))

;; = with function/macro/special form calls are essentially syntax sugar

(defn fun [x] (+ x x))

;; if you want to test equality, use ==

(= 0 0);; becomes (= 0 0)

;; now we know enough to write more complete programs

;; single arg
(defn hello [name]  
  (str "Hello, " name)) 


;; no args
(defn hello-hello  
  [] (hello "Hello")) 


;; default/keyword arg
(defn hello-dallas [& :keys [name] :or {name "Dallas"}]  
  (hello name)) 


;; multiple args and multiple expressions
(let [x 1 y 1]  
  (let [total (+ x y)]  
    (println total)  
    total))  
  
;; outer let

(add 1 2)
(add 1 2)