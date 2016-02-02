package com.enjapan.knp

import cats.data.Xor
import com.enjapan.knp.models.BList

import sys.process._

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
object KNP {

  val KNP_PATH = sys.env.getOrElse("KNP_PATH", "knp")
  val JUMAN_PATH = sys.env.getOrElse("JUMAN_PATH", "juman")

  def main(args:Seq[String]):Unit = {
    val knp = new KNP
    knp(args.head) match {
      case Xor.Left(e) => throw e
      case Xor.Right(bList) =>
        bList.roots.foreach {
          r =>
            r.traverse (println)
        }
    }
  }
}

class KNP {
  import KNP._
  val parser = new KNPParser()

  def apply(text: String): Xor[ParseException, BList] = {
    val lines = Seq("echo", text) #| JUMAN_PATH #| Seq(KNP_PATH, "-tab") lineStream

    parser.parse(lines)
  }
}
