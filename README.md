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

## license

Copyright © 2015 Aaron Lebo

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
