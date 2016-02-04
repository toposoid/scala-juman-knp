package com.enjapan.knp.models

import com.enjapan.juman.models.Morpheme

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
case class Pas(cfid: String, arguments: Map[String, PredicateArgumentAnalysis])

case class PredicateArgumentAnalysis(
  `case`: String,
  caseType: String,
  arg: String,
  argNo:Int,
  argSentId:String)

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
