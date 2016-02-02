package com.enjapan.knp

import org.scalatest.{Matchers, FunSuite}

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
class KNPParserTest extends FunSuite with Matchers {

  test("testParse") {

    val stream = getClass.getResourceAsStream("/knp_output")
    val lines = scala.io.Source.fromInputStream( stream ).getLines().toIterable

    val parser = new KNPParser()

    val bList = parser.parse(lines)

    bList.bunsetsuList.size should be (7)

  }

}
