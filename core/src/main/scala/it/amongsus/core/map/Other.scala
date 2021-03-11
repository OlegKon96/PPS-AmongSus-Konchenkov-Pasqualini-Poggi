package it.amongsus.core.map

import it.amongsus.core.util.Point2D

/**
 * Trait that represents "other" objects in the game
 */
trait Other extends Tile

object Other{
  def apply(position: Point2D): Other = OtherImpl(position)

  private case class OtherImpl(override val position: Point2D) extends Other

}