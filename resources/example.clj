#_(This is a comment (note: two semicolons and not one unlike Clojure).)

#_(prose has literals much like Clojure.)

1#_(integer)
1.0#_(float)
"sup"#_(string)
#".*"#_(regex)
:yeah#_(keyword)
[1 2 3]#_(vector)
#{:a :b :c}#_(set)
(1 2 3)#_(list)
()#_(empty list)
(1)#_(list with single item)
1#_(the expression 1)

#_(The list syntax is inspired by Python's tuple syntax.)

#_(Symbols - like Clojure, prose allows for a wide variety of identifiers not available in other languages.)

awesome?
awesome!
so-good
<alright>

#_(Pairs are a syntax shortcut, currently they are spliced into whatever form they are placed, with symbols on the left side getting converted to keywords.)

:a 1
"yes" "no"  #_("yes" "no")
0 :a  #_(0 :a)

#_(This allows for nice looking hash-maps, default arguments, and other niceties.)

{:a 1 "yes" "no" 0 :a}

#_(Pairs may end up getting compiled down to a concerete form.) 

#_(Function/macro/special form calls - about what you'd except.)

(println 1)
(println 1 2 3)
(if true :yes :no)
(my-function "test" :test "this")#_(keyword args, when applicable)

#_(Function/macro/special form application - the magic of prose; defined as "exp non-newline-whitespace exp". The earlier calls can be expressed using this syntax.)

(println 1)
(println 1 2 3)
(if true :yes :no)
(my-function "test" :test "this")

(println (dec (inc 1)))#_(can also be chained)

#_(Newlines - if you haven't noticed, newlines denote multiple expressions, much like Ruby. You can also use semicolons. Multiople expressions get wrapped in a Clojure do form except where that is uncessary (let, defn, etc bodies).)

(if true :yes  
  (do (println :no)  
    :no)) 

(if true :yes (do (println :no) :no))


#_(Operators - these are symbols which contain only non-alphanumeric characters.)

+
-
*
/

?
!!!
<>

#_(These are only some of the possibilties. Clojure uses some chars such as @ for special syntax, it is possible these may be kept available in prose for use in operators.)

#_(Operations - operations are defined as "exp space operator space exp". This allows operators to still be used like regular symbols in many contexts.)

(def sum (partial reduce +))

#_(When they are used in operations, with a single exception (as we'll see), there's no precedence.)

(* (+ 1 1) 2)#_(= 4)

#_(Lack of precedence is a feature, not a bug. It keeps both the implementation and use simple. Use parens to dictate flow.)

(+ 1 (* 1 2))#_(= 3) 


#_(Assignment - prose needs some kind of assignment operator to stay sexy. Assignment is dictated by "=" and has lower precedence than all other operations. If you are looking to test equality, that is "==".)