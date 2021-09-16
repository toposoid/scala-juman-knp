package com.enjapan.juman

import com.enjapan.SocketServer

import scala.sys.process._

/**
  * Created by Ugo Bataillard on 3/14/16.
  */
object Juman {

  val JUMAN_PATH = helpers.envProps.getOrElse("JUMAN_PATH", "juman")
  val JUMAN_FLAGS = Seq()
  val JUMAN_SERVER_FLAGS = Seq("-F", "-S")
  val DEFAULT_JUMAN_HOST = "127.0.0.1"
  val DEFAULT_JUMAN_PORT = 32000

  def startServer(command:Seq[String] = JUMAN_PATH +: JUMAN_SERVER_FLAGS) :Process = SocketServer.startServer(command)
  def withServer[T](command: Seq[String] = JUMAN_PATH +: JUMAN_SERVER_FLAGS)(f: () => T):T = SocketServer.withServer(command)(f)

  import com.enjapan.SocketClient._

  def withClient[T](host:String = DEFAULT_JUMAN_HOST, port:Int = DEFAULT_JUMAN_PORT) (f: JumanClient => T)  = {
    withSocket(host,port) { s =>
      val c = new JumanClient(s.getInputStream, s.getOutputStream)
      c.init()
      f(c)
    }
  }
}
