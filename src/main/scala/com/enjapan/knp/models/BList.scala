package com.enjapan.knp.models

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
case class BList(pattern:String, comment: String, sid:String, bunsetsuList: IndexedSeq[Bunsetsu]) {

  lazy val tagList = {
    for{
      bnst <- bunsetsuList
      tag <- bnst.tags
    } yield tag
  }

  def setParentChild() =  {
    for (bnst <- bunsetsuList) {

      if (bnst.parentId == -1) {
        bnst.parent = None
      }
      else {
        val parent = bunsetsuList(bnst.parentId)
        bnst.parent = Some(parent)
        parent.addChild(bnst)
      }

      for (tag <- bnst.tags) {
        if (tag.parentId == -1) {
          tag.parent = None
        }
        else {
          val parent = tagList(bnst.parentId)
          tag.parent = Some(parent)
          parent.addChild(tag)
        }
      }
    }
    ()
  }

  setParentChild()

  override def toString: String = {
    return String.format("%s\n%s%s\n", comment, bunsetsuList.mkString(","), pattern)
  }

  lazy val root = bunsetsuList.find(_.parentId == -1).get
  lazy val roots = bunsetsuList.filter(_.parentId == -1)

}
