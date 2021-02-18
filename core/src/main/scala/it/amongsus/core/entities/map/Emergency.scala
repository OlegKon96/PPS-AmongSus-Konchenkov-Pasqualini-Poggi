package it.amongsus.core.entities.map

import it.amongsus.core.entities.util.Point2D

/**
 * Trait that manages the emergency button item of the game
 */
trait Emergency extends Wall{
  def callEmergency() : Unit
}

object Emergency{
  def apply(position: Point2D): Emergency = EmergencyImpl(position)

  private case class EmergencyImpl(override val position: Point2D) extends Emergency {
    override def callEmergency(): Unit = ???
  }
}