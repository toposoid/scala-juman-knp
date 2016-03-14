package com.enjapan
package knp

import juman.{Juman, JumanClient}
import com.enjapan.backports._
import cats.data.Xor
import com.enjapan.SocketClient._
import com.enjapan.juman.Juman._
import com.enjapan.knp.models.BList

import scala.language.postfixOps
import sys.process._

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
object KNP {

  val KNP_PATH = helpers.envProps.getOrElse("KNP_PATH", "knp")

  val KNP_FLAGS = List("-tab")
  val KNP_SERVER_FLAGS = "-F" :: KNP_FLAGS
  val DEFAULT_KNP_HOST = "127.0.0.1"
  val DEFAULT_KNP_PORT = 31000


  def main(args: Seq[String]): Unit = {
    val knp = new KNPCli
    knp(args.head) match {
      case Xor.Left(e) => throw e
      case Xor.Right(bList) =>
        bList.roots.foreach { _ traverse println }
    }
  }

  def startServer(command: Seq[String] = KNP_PATH +: KNP_SERVER_FLAGS): Process = SocketServer.startServer(command)
  def withServer[T](command: Seq[String] = KNP_PATH +: KNP_SERVER_FLAGS)(f: () => T): T = SocketServer.withServer(command)(f)

  def withServerBackedClient[T](f: KNPClient => T): T = {
    Juman.withServer() { () =>
      withServer() { () =>
        withClient()(f)
      }
    }
  }

  def withClient[T](knpHost:String = DEFAULT_KNP_HOST, knpPort:Int = DEFAULT_KNP_PORT, jumanHost:String = DEFAULT_JUMAN_HOST, jumanPort:Int = DEFAULT_JUMAN_PORT)(f: KNPClient => T):T = {
    Juman.withClient(jumanHost, jumanPort) { jumanClient =>
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

trait KNP {
  val parser = new KNPParser()
  def apply(text:String) = parse(text)
  def parse(text:String): Xor[ParseException, BList]
}

class KNPCli(knpCommand: Seq[String] = KNP.KNP_PATH +: KNP.KNP_FLAGS, jumanCommand: Seq[String] = Seq(juman.Juman
  .JUMAN_PATH)) extends KNP {

  override def parse(text: String): Xor[ParseException, BList] = {
    val lines = Seq("echo", text) #| jumanCommand #| knpCommand lineStream

    parser.parse(lines)
  }
}
