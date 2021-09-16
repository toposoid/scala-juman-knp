package com.enjapan.juman

import com.enjapan.juman.models.Morpheme
import com.enjapan.knp.ParseException

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
class JumanParser {

}

object JumanParser {

  val splitOutsideQuotesReges = """[^\s"]+|"([^"]*)"""".r

  def parseMorpheme(spec:String): Either[ParseException, Morpheme] = {
    splitOutsideQuotesReges.findAllMatchIn(spec).map { m =>
      m.group(0) match {
        case null => m.source.toString
        case x => x
      }
    }.toList match {
      case midasi::yomi::genkei::hinsi::hinsiId::bunrui::bunruiId::katuyou1::katuyou1Id::katuyou2::katuyou2Id::imis::fstring::_ =>
        Right(Morpheme(
          midasi,
          yomi,
          genkei,
          hinsi,
          hinsiId.toInt,
          bunrui,
          bunruiId.toInt,
          katuyou1,
          katuyou1Id.toInt,
          katuyou2,
          katuyou2Id.toInt,
          imis,
          fstring
        ))
      case _ => Left(ParseException(s"Could not parse morpheme line: $spec"))
    }

  }


}
