package com.enjapan
package knp

import juman.JumanClient
import java.io.{InputStream, OutputStream}

import cats.data.Xor
import com.enjapan.knp.models.BList

class KNPClient(jumanClient: JumanClient, in: InputStream, out:OutputStream) extends SocketClient(in, out) with KNP {
  val command = "RUN -tab"

  override def parse(text: String): Xor[ParseException, BList] = {
    val jumanRes = jumanClient.run(Iterator(text))
    val knpRes = run(jumanRes)
    parser.parse(knpRes.toIterable)
  }
}

