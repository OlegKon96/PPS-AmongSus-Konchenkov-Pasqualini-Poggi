package it.amongsus.core.entities.map

import it.amongsus.core.entities.util.Point2D

trait Boundary extends Wall

object Boundary{
  def apply(position: Point2D): Boundary = BoundaryImpl(position)
}

case class BoundaryImpl(override val position: Point2D) extends Boundary {

}
