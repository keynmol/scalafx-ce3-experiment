# ScalaFX, Scala3, and CE3

This is a very trivial experiment to experiment with

1. [ScalaFX](https://github.com/scalafx/scalafx)
2. [Cats Effect 3](https://typelevel.org/cats-effect/)
3. [Scala 3](https://docs.scala-lang.org/scala3/new-in-scala3.html)

This project is Twitterware - it was deemed completed after the
[tweet](https://twitter.com/velvetbaldmime/status/1403074760406405123) was
published.

Anything that can be described in 140 characters is not worth having.

## Running

Why?

```scala
sbt> run
```

Should be enough (if you're using JDK11).

## Future

No idea. Having such a powerful set of libraries for concurrency and
asynchrony,
along with a purely single threaded JavaFX should make for a fun experiment.

One unsolved issue is how to hack JavaFX's events to be able to work with fs2
`Topic` and Scala 3's enums.
