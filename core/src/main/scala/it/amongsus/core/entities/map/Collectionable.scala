package it.amongsus.core.entities.map

import java.awt.Color

import it.amongsus.core.entities.Entity
import it.amongsus.core.entities.util.Point2D

trait Collectionable extends Entity{
  def collect(): Unit
}

object Collectionable{
  def apply(position: Point2D): Collectionable = CollectionableImpl(Color.YELLOW, position)}

case class CollectionableImpl(override val color: Color, override val position: Point2D) extends Collectionable {
  override def collect(): Unit = ???
}