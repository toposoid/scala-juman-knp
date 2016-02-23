package com.enjapan

import scala.sys.process.ProcessBuilder

/**
  * Created by Ugo Bataillard on 2/23/16.
  */
package object helpers {

  implicit class ProcessBuilderCompat(val p:ProcessBuilder){

    def lineStream: Stream[String] = {
      p.lines
    }

  }

}
