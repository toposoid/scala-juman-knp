# scala-juman-knp

[![Build Status](https://travis-ci.org/en-japan/scala-juman-knp.svg?branch=master)](https://travis-ci.org/en-japan/scala-juman-knp)
[![Coverage Status](https://coveralls.io/repos/github/en-japan/scala-juman-knp/badge.svg?branch=master)](https://coveralls.io/github/en-japan/scala-juman-knp?branch=master)

Scala wrapper around [JUMAN](http://nlp.ist.i.kyoto-u.ac.jp/index.php?cmd=read&page=JUMAN) and [KNP](http://nlp.ist.i.kyoto-u.ac.jp/EN/?KNP).

## How to install

In your `build.sbt`:
```
resolvers += "en-japan Maven OSS" at "http://dl.bintray.com/en-japan/maven-oss"

libraryDependencies += "com.enjapan" %% "scala-juman-knp" % "0.0.5"
```

## How to use

### Normal mode
Example:
```scala
import com.enjapan.knp.KNP

val knp = new KNPCli()
val blist = knp("京都大学に行った。")

blist.root.traverse(println)
```

### Server mode
```scala
import com.enjapan.knp.KNP

KNP.withServerBackedClient { knp =>
  (1 to 1000).foreach { _ =>
    val blist = knp.parse("京都大学に行った。")
    blist.root.traverse(println)
  }
}
```

