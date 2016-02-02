package com.enjapan.knp.models

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
case class Bunsetsu(
  parentId:Int,
  dpndtype:String,
  fstring:String,
  repName:Option[String],
  tags:Seq[Tag]) extends KNPNode[Bunsetsu]

