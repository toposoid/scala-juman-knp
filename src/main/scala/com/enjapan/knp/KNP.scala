package com.enjapan.knp

import com.enjapan.knp.models.BList

import sys.process._

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
object KNP extends App {

  val KNP_PATH = sys.env.getOrElse("KNP_PATH", "knp")
  val JUMAN_PATH = sys.env.getOrElse("JUMAN_PATH", "juman")

  val knp = new KNP
  val bList = knp(args.head)

  bList.roots.foreach { r =>
    r.traverse(println)
  }

}

class KNP {
  import KNP._
  val parser = new KNPParser()

  def apply(text: String): BList = {
    val lines = Seq("echo", text) #| JUMAN_PATH #| Seq(KNP_PATH, "-tab") lineStream
    
    parser.parse(lines)
  }
}
