package com.enjapan
package knp

import juman.JumanClient

import java.io.{InputStream, OutputStream}

class KNPClient(jumanClient: JumanClient, in: InputStream, out:OutputStream) extends SocketClient(in, out) {
  val command = "RUN -tab"

  val parser = new KNPParser()

  def parse(s:String) = {
    val jumanRes = jumanClient.run(Iterator(s))
    val knpRes = run(jumanRes)
    parser.parse(knpRes.toIterable)
  }
}

object KNPClient {

  import SocketClient._
  import JumanClient._

  val DEFAULT_KNP_HOST = "127.0.0.1"
  val DEFAULT_KNP_PORT = 31000

  def withClient[T](knpHost:String = DEFAULT_KNP_HOST, knpPort:Int = DEFAULT_KNP_PORT, jumanHost:String = DEFAULT_JUMAN_HOST, jumanPort:Int = DEFAULT_JUMAN_PORT)(f: KNPClient => T):T = {
    JumanClient.withClient(jumanHost, jumanPort) { jumanClient =>
      withClient(jumanClient)(knpHost, knpPort) { c => f(c) }
    }
  }

  def withClient[T](jumanClient: JumanClient)(host:String, port:Int)(f: KNPClient => T):T = {
    withSocket(host,port) { s =>
      val c = new KNPClient(jumanClient, s.getInputStream, s.getOutputStream)
      c.init()
      f(c)
    }
  }

}