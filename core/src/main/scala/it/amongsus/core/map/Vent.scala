package it.amongsus.core.map

import it.amongsus.core.util.Point2D

/**
 * Trait that represents the vent of the game.
 */
trait Vent extends Floor

object Vent{
  def apply(position: Point2D): Vent = VentImpl(position)

  private case class VentImpl(override val position: Point2D) extends Vent
}