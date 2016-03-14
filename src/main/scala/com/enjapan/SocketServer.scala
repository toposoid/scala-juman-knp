package com.enjapan

import scala.sys.process._
import scala.util.Try

/**
  * Created by Ugo Bataillard on 3/14/16.
  */
object SocketServer {

  def startServer(command:Seq[String]): Process = {
    val bgProc = command.run()
    sys addShutdownHook {
      bgProc.destroy
    }
    bgProc
  }

  def withServer[T](command: Seq[String])(f: () => T): T = {
    val proc = startServer(command)
    Thread.sleep(100)
    val res = Try { f() }
    proc.destroy()
    res.get
  }

}
