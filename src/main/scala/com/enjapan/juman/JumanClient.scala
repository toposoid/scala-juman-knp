package com.enjapan
package juman

import java.io.{InputStream, OutputStream}

/**
  * Created by Ugo Bataillard on 3/9/16.
  */
class JumanClient(in:InputStream, out:OutputStream) extends SocketClient(in, out) {
  val command = "RUN -e2"
}

object JumanClient {

  import SocketClient._

  val DEFAULT_JUMAN_HOST = "127.0.0.1"
  val DEFAULT_JUMAN_PORT = 32000

  def withClient[T](host:String = DEFAULT_JUMAN_HOST, port:Int = DEFAULT_JUMAN_PORT) (f: JumanClient => T)  = {
    withSocket(host,port) { s =>
      val c = new JumanClient(s.getInputStream, s.getOutputStream)
      c.init()
      f(c)
    }
  }
}
