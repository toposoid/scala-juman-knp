package com.enjapan.knp.models

import com.enjapan.juman.models.Morpheme

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
case class Pas(cfid: String, arguments: Map[String, PredicateArgumentAnalysis])

case class PredicateArgumentAnalysis(
  relationName: String,
  relationType: String,
  argWord: String,
  argId:Int,
  argSentId:String) {

  private var _arg:Tag = null

  protected[knp] def arg_=(tag:Tag): Unit = {
    _arg = tag
  }

}

case class Rel(
  `type`: String,
  target: String,
  sid: String,
  id: Option[Int],
  mode: Option[String]
)

case class Tag(
  parentId:Int,
  dpndtype:String,
  fstring:String,
  paType:PAType,
  morphemes: Seq[Morpheme],
  features:Map[String,String],
  rels: Seq[Rel],
  pas:Option[Pas]) extends KNPNode[Tag] {

  def surface:String = morphemes.map(_.midasi).mkString

}
