package it.amongsus.core.map

import it.amongsus.core.util.Point2D

/**
 * Trait that manages the floor of the game
 */
trait Floor extends Tile

object Floor{
  def apply(position: Point2D): Floor = FloorImpl(position)

  private case class FloorImpl(override val position: Point2D) extends Floor

}