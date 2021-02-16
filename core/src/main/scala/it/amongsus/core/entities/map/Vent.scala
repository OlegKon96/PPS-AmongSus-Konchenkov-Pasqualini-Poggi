package it.amongsus.core.entities.map

import it.amongsus.core.entities.util.Point2D

trait Vent extends Floor{
  def useVent(): Unit
}

object Vent{
  def apply(position: Point2D): Vent = VentImpl(position)
}

case class VentImpl(override val position: Point2D) extends Vent {
  override def useVent(): Unit = ???
}