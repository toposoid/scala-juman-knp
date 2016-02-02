package com.enjapan.knp.models

/**
  * Created by Ugo Bataillard on 2/2/16.
  */
class KNPNode[T <: KNPNode[T]] { self:T =>

  private var _parent: Option[T] = None
  private var _children: List[T] = List[T]()

  def parent = _parent
  def children = _children

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
