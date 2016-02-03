# scala-juman-knp

[![Build Status](https://travis-ci.org/en-japan/scala-juman-knp.svg?branch=master)](https://travis-ci.org/en-japan/scala-juman-knp)
[![Coverage Status](https://coveralls.io/repos/github/en-japan/scala-juman-knp/badge.svg?branch=master)](https://coveralls.io/github/en-japan/scala-juman-knp?branch=master)

Scala wrapper around [JUMAN](http://nlp.ist.i.kyoto-u.ac.jp/index.php?cmd=read&page=JUMAN) and [KNP](http://nlp.ist.i.kyoto-u.ac.jp/EN/?KNP).

## How to install

In your `build.sbt`:
```
resolvers += "en-japan Maven OSS" at "http://dl.bintray.com/en-japan/maven-oss"

libraryDependencies += "com.enjapan" %% "scala-juman-knp" % "0.0.1"
```

## How to use

Example:
```scala
import com.enjapan.knp.KNP

val knp = new KNP()
val blist = knp("京都大学に行った。")

blist.root.traverse(println)
```
