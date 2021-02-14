package it.amongsus.model.entities.map

import it.amongsus.model.entities.Entity
import it.amongsus.model.entities.util.Point2D

trait Collectionable extends Entity{
  def collect(): Unit
}

object Collectionable{
  def apply(position: Point2D): Collectionable = CollectionableImpl(position)
}

case class CollectionableImpl(override val position: Point2D) extends Collectionable {
  override def collect(): Unit = ???
}