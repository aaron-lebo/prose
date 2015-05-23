# prose 

*No subject is terrible if the story is true, if the __prose__ is clean and honest, and if it affirms courage and grace under pressure.* - Ernest Hemingway as played by Corey Stoll in *Midnight in Paris*

## tldr

```
buffalo = defn(end,
  let(strings: 1 * 8 repeat("buffalo"),
    idxs: [0, 2, 6],
    f: fn(idx, itm,
      if(idx2 fn(idx2 == idx) some(idxs),
        itm string/capitalize,
        itm
      )
    ),
    res: f map-indexed(strings),
    " " string/join(res) str(end case(
      period: ".",
      qmark: "?",
      "!"
    ))
  )
)

:period buffalo
buffalo(:qmark)

excited-buffalo = buffalo partial(:exmark)
excited-buffalo()
```

prose's syntax is based on Io and Ioke. Unlike Io and Ioke, it is not particularly object-oriented. Instead it is based on and compiles to readable Clojure and ClojureScript. This allows us to hook into the JVM and anything ClojureScript runs on (which is anywhere JavaScript runs), and gives us very powerful features like multimethods, protocols, and the async library. Oh, and we also get macros, without the traditional lisp syntax.

The core idea behind prose is that in Clojure or any other functional language, function application is the most important aspect of the language, it should also be the most important part of the syntax. As such, whitespace denotes function application.

```
[1, 2, 3] sum + 1 println
```

This is equivalent to the following Clojure code.

```
(println (+ 1 (sum [1 2 3])))
```

Notice how the prose example allows the use of operators and also reads more like natural language.

If you want to know more about the language, [resources/test.pr](https://github.com/aaron-lebo/prose/blob/master/resources/example.pr) has numerous examples and currently considered the canonical test of compiler. The results of compiling that file are found in [resources/example.clj](https://github.com/aaron-lebo/prose/blob/master/resources/example.clj). The parser is written using the incredible [Instaparse](https://github.com/Engelberg/instaparse) library. I cannot overstate how cool it is. It takes [this grammar definition](https://github.com/aaron-lebo/prose/blob/master/resources/grammar.bnf) and generates an AST. It is easily the best lexing/parsing tool I've ever used. The compiler attempts to keep expressions on the same line when going from input to output file. It isn't perfect, but Clojure has terrible error messages anyway, so it can't hurt too much. I'm joking. Or am I? 

## why

Lisp dialects are often talked about as though they were religious experiences that everyone should experience at one point in their lifetime. You can even find John McCarthy's creation of the original LISP in 1956 spoken of in terms of discovery, as though lisp was an immutable law or truth like gravity, just waiting for human beings to become enlightened enough to realize it was there.

Perhaps this is for good reason. There are many features in the original that we are only just taking for granted in modern languages today. Then you've got Scheme and its incredibly clean, small, yet powerful core, and Common Lisp which has features that a lot of modern dynamic languages are still missing: speed, true compilation, the ability to jack into and modify a running program. The lisp family of languages is fascinating and any student of programming is doing a disservice to themselves if they haven't gone back and tried to understand what it all means.

However, lisp's greatest advantage, most distinguishing feature, as well as greatest disadvantage as far as adoption goes is syntax. Syntax is a superficial thing. Spend enough time working in a language and you can get used to anything. Note: the quote by Matt Damon's character in *The Departed* comes to mind: *I'm fucking Irish, I'll deal with something being wrong for the rest of my life*. Lisp syntax isn't even particularly bad. People mock it because of all the parentheses, but the fact of the matter is, it has no more parentheses than a lot of other mainstream languages and its lack of visual variety allows for one very powerful thing: macros.

Despite this, there is no doubt that lisp syntax can be jarring, especially in contrast to just about every other language. Complaints about syntax may be superficial, and they may be things that you get over after continued use, but many people never reach this point because they see the syntax and never give it a second chance. You can complain all day about how this is intellectually lazy, but its just how human beings work. People have been struggling for years to figure out how to get lisp to be adopted in the mainstream. It really is pretty simple: change human nature and the masses will start using lisp.

Yes, that was a joke. The thing is, despite my own continued use of lisp, there is one aspect of the syntax that I do believe has a detrimental effect on readability. This effect is nesting. Let's take a closer look. Say you want to take an array of numbers, sum them up, and one to that total, and then print them. In many mainstream languages, the code reads very similarly to that actual description. With the correct methods defined, the following code works in Python, Ruby, JavaScript, and numerous other languages.

```
([1, 2, 3].sum() + 1).print()
```

Aside from the fact that lisps do not have true operators, in lisps this reads inside-out.

```
(print (+ 1 (sum [1 2 3])))
```

The former example reads much more like UNIX piping: one value gets passed to the next. I personally believe that this is intuitively easier for most people to follow, at least if you read a language that reads from left to right, and perhaps even if you do not. Outside of just that, you constantly hear people complain about lisp having poor syntax for math; we are trained from youth to use operators. These are hard habits to break.

Enough bashing on lisp. The question is can we can take three great advantages of lisp syntax: uniformity, homoiconicity (for easy macros), and simplicity, and incorporate true operators while eliminating too much nesting? We can, and prose is an attempt at this.

## what

prose is heavily inspired by Io and Ioke. Io is Steve Dekorte's minimalist and radical experiment in language design and Ioke is Ola Bini's later work, inspired by Io, which runs on the JVM. Both of these languages are incredibly elegant; you might call them art house programming languages. They are very aesthetically pleasing, and that in and of itself is a noble goal. David Heinemeier Hanson, the creator of Rails, used to make a big deal about "beautiful code". This might seem silly and even pretentious, but I firmly believe that coding is both a creative and artistic endeavor. In an industry that is so often thought of in such mechanical and cold terms, sometimes we forget about the artistry and magic of putting together the right algorithm and then writing that code in an elegant manner. You can do this in any language, but some languages emphasize it more than others.

But, hey, the underlying mechanics of it all do matter. Languages are more than syntax. Io and Ioke are incredibly, perhaps overly dynamic languages. They are both difficult to efficiently compile. Being radical, they also have some features which deviate from more mainstream languages. This makes adoption difficult. 

What we want is a language which captures the essence of their syntax while still aligning with more traditional semantics. At the heart of the Io family of languages is the use of whitespace. Some of you will freak out about this, but it is not whitespace the way Python uses it. In Io, methods are invoked via whitespace. Our earlier Python/Ruby/JS/etc example might look something more like the following.

```
list(1, 2, 3) sum + 1 println
``` 

This eliminates a lot of noise and makes the language read more like natural language than many others. Multiple expressions are denoted by newlines.

```
arr := list(1, 2, 3)
res := arr sum + 1
res println
```

Now, Io and Ioke are both prototype-based languages. Within the context of mainstream lisps this doesn't make a lot of sense. Some lisps, like Common Lisp even are object-oriented or make object orientation a core aspect of the language. Fundamentally, though, they are based around functions. Functions can also be expressed in a piping manner. Clojure even has a macro which does this, the threading operator.

```
(-> [1, 2, 3] sum (+ 1) println)
```

This works great, but why should it be an additional operator that you have to pull out from time to time? Function application is the most important aspect of a lisp, why should it not be the most favored syntax? Let's take Io's use of whitespace as method invocation and use it for function application.

```
[1, 2, 3] sum + 1 println

```

If you are having trouble following, the vector of numbers get passed as the first argument to sum which is then called, the result of that is passed in as the first argument to + which is called, and the final result get passed to println which is then called.

This is prose, and it naturally compiles down to Clojure and ClojureScript. Why Clojure and ClojureScript? The Clojure family of languages are modern lisps which can harness the power and ecosystems of Java and JavaScript respectively. They are based around immutability and functional programming. They also have a lot of really interesting features like multimethods, protocols, and the async library. I'm of the personal opinion that feature for feature they are the most well-designed dynamic languages today. They also make a really great compilation target because the syntax is so simple and everything is an expression.

To sum it all up: prose is a simple language, inspired by Io and Ioke, that compiles to Clojure and ClojureScript.

## the future

This is partially a pet project, partially an experiment in syntax, and partially a test to gauge whether other people are interested in working with me on making this truly usable. I have no formal training in compilers and as such prose is the result of years of fumbling around. The grammar definition and compiler could probably be much cleaner and simpler. If there was enough interest, it might make more sense to stop using Clojure and ClojureScript as compilation targets and instead to hook into the same code generation tools they use. They are seen as compilation targets instead of hard standards first and foremost, but interoperability is very important. Their communities are too smart, it does not make sense to diverge. Additionally, the future seems to be with gradual typiong. It would be intersting to make a project like Typed Clojure a core part of the syntax.

Anyway, I'd love to make this more than just an experiment. New Github issues and pull requests are very appreciated.

## license

Copyright © 2015 Aaron Lebo

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
