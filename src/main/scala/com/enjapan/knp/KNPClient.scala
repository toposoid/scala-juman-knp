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

