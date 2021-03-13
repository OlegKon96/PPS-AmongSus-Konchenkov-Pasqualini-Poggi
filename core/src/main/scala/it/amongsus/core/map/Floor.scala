package it.amongsus.core.map

import it.amongsus.core.util.Point2D

/**
 * Trait that represents the floor of the game map
 */
trait Floor extends Tile

object Floor{
  def apply(position: Point2D): Floor = FloorImpl(position)

  private case class FloorImpl(override val position: Point2D) extends Floor
}