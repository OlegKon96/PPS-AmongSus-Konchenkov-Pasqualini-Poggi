package it.amongsus.core.map

import it.amongsus.core.util.Point2D

/**
 * Trait that represents the Boundary of the game.
 */
trait Boundary extends Wall

object Boundary{
  def apply(position: Point2D): Boundary = BoundaryImpl(position)

  private case class BoundaryImpl(override val position: Point2D) extends Boundary
}