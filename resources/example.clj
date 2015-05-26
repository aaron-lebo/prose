 ;; This is a comment (note: two semicolons and not one unlike Clojure).

 ;; prose has literals much like Clojure.

1 ;; integer
1.0 ;; float
"sup" ;; string
#".*" ;; regex
:yeah ;; keyword
[1 2 3] ;; vector
#{:a :b :c} ;; set
(1 2 3) ;; list
() ;; empty list
(1) ;; list with single item
1 ;; the expression 1

 ;; The list syntax is inspired by Python's tuple syntax.

 ;; Symbols - like Clojure, prose allows for a wide variety of identifiers not available in other languages.

awesome?
awesome!
so-good
<alright>

 ;; Pairs are a syntax shortcut, currently they are spliced into whatever form they are placed, with symbols on the left side getting converted to keywords.

:a 1
"yes" "no"   ;; "yes" "no"
0 :a   ;; 0 :a

 ;; This allows for nice looking hash-maps, default arguments, and other niceties.

{:a 1 "yes" "no" 0 :a}

 ;; Pairs may end up getting compiled down to a concerete form. 

 ;; Function/macro/special form calls - about what you'd except.

(println 1)
(println 1 2 3)
(if true :yes :no)
(my-function "test" :test "this") ;; keyword args, when applicable

 ;; Function/macro/special form application - the magic of prose. defined as exp non-newline-whitespace exp. The earlier calls can be expressed using this syntax.

(println 1)
(println 1 2 3)
(if true :yes :no)
(my-function "test" :test "this")

(println (dec (inc 1))) ;; can also be chained

 ;; Newlines - if you haven't noticed, newlines denote multiple expressions, much like Ruby. You can also use semicolons. Multiople expressions get wrapped in a Clojure do form except where that is uncessary (let, defn, etc bodies).

(if true :yes  
    (do (println :no)  
        :no)) 

(if true :yes (do (println :no) :no))


 ;; Operators - these are symbols which contain only non-alphanumeric characters.

+
-
*
/

?
!!!
<>

 ;; These are only some of the possibilties. Clojure uses some chars such as @ for special syntax, it is possible these may be kept available in prose for use in operators.

 ;; Operations - operations are defined as "exp space operator space exp". This allows operators to still be used like regular symbols in many contexts.

(def sum (partial reduce +))

 ;; When they are used in operations, with a single exception (as we'll see), there's no precedence.

(* (+ 1 1) 2) ;; = 4

 ;; Lack of precedence is a feature, not a bug. It keeps both the implementation and use simple. Use parens to dictate flow.

(+ 1 (* 1 2)) ;; = 3 


 ;; Assignment - prose needs some kind of assignment operator to stay sexy. Assignment is dictated by "=" and has lower precedence than all other operations. If you are looking to test equality, that is "==".