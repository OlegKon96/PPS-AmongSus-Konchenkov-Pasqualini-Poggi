package it.amongsus.core.entities.map

import it.amongsus.core.entities.Entity
import it.amongsus.core.entities.util.Point2D

trait Collectionable extends Entity{
  def collect(): Unit
}

object Collectionable{
  def apply(position: Point2D): Collectionable = CollectionableImpl(position)
}

case class CollectionableImpl(override val position: Point2D) extends Collectionable {
  override def collect(): Unit = ???
}