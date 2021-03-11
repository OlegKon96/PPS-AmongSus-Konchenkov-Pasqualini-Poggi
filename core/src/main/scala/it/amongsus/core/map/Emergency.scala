package it.amongsus.core.map

import it.amongsus.core.util.Point2D

/**
 * Trait that manages the emergency button item of the game
 */
trait Emergency extends Wall

object Emergency{
  def apply(position: Point2D): Emergency = EmergencyImpl(position)

  private case class EmergencyImpl(override val position: Point2D) extends Emergency

}