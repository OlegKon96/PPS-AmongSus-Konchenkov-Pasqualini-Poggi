package it.amongsus.core.entities.map

import it.amongsus.core.entities.util.Point2D

/**
 * Trait that manages the wall of the game
 */
trait Wall extends Tile

object Wall{
  def apply(position: Point2D): Wall = WallImpl(position)

  private case class WallImpl(override val position: Point2D) extends Wall
}