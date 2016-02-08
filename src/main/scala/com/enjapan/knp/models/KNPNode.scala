package com.enjapan.knp.models

/**
  * Created by Ugo Bataillard on 2/2/16.
  */

sealed trait PAType
case object Argument extends PAType
case class Predicate(predicateType:String) extends PAType

abstract class KNPNode[T <: KNPNode[T]] { self:T =>

  private var _parent: Option[T] = None
  private var _children: List[T] = List[T]()

  def parent = _parent
  def children = _children

  def features:Map[String,String]
  def paType:PAType
  def repName:Option[String] = features.get("正規化代表表記")

  protected[knp] def parent_=(p:Option[T]): Unit = {
    _parent = p
  }

  protected[knp] def addChild(child: T) {
    _children ::= child
  }

  def traverse(f: T => Unit):Unit = {
    f(self)
    children.foreach(_.traverse(f))
  }

}
