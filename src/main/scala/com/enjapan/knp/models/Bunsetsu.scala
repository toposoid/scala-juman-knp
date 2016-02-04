package com.enjapan.knp.models

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
case class Bunsetsu(
  parentId: Int,
  dpndtype: String,
  fstring: String,
  paType: PAType,
  features: Map[String,String],
  tags: Seq[Tag]) extends KNPNode[Bunsetsu]

