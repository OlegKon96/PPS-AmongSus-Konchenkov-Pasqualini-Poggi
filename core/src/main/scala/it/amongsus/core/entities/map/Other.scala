package it.amongsus.core.entities.map

import it.amongsus.core.entities.util.Point2D

/**
 * Trait that represents "other" objects in the game
 */
trait Other extends Tile

object Other{
  def apply(position: Point2D): Other = OtherImpl(position)

  private case class OtherImpl(override val position: Point2D) extends Other
}