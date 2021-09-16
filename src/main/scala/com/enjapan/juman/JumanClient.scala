package com.enjapan.juman

import com.enjapan.SocketClient

import java.io.{InputStream, OutputStream}

/**
  * Created by Ugo Bataillard on 3/9/16.
  */
class JumanClient(in:InputStream, out:OutputStream) extends SocketClient(in, out) {
  val command = "RUN -e2"
}
