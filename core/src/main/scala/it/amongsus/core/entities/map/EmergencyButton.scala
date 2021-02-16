package it.amongsus.core.entities.map

import it.amongsus.core.entities.util.Point2D

trait EmergencyButton extends Wall{
  def callEmergency() : Unit
}

object EmergencyButton{
  def apply(position: Point2D): EmergencyButton = EmergencyButtonImpl(position)
}

case class EmergencyButtonImpl(override val position: Point2D) extends EmergencyButton {
  override def callEmergency(): Unit = ???
}