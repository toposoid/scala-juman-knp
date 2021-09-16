package com.enjapan

import java.io.{InputStream, OutputStream, PrintStream}
import java.net.Socket

import scala.io.BufferedSource
import scala.util.Try

/**
  * Created by Ugo Bataillard on 3/9/16.
  */
abstract class SocketClient(input:InputStream, output:OutputStream) {

  val command:String
  val EOS:String = "EOS"

  private lazy val in = new BufferedSource(input).getLines().drop(2)
  private val out = new PrintStream(output)

  def init() = {
    out.println(command)
  }

  def run(lines: Iterator[String]) = {
    lines foreach out.println
    out.flush()
    in.takeWhile(!_.startsWith(EOS)) ++ List(EOS)
  }

}

object SocketClient {

  def withSocket[T](host:String, port:Int) (f: Socket => T): T = {
    Try {
      val socket = new Socket(host, port)

      val res = Try {
        f(socket)
      }

      if (!socket.isClosed) {
        try {
          socket.close()
        } catch {
          case t: Throwable =>
        }
      }
      res
    }.flatten.get
  }
}