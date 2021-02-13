package it.amongsus.model.entities.map

trait Vent extends Floor{
  def useVent(): Unit
}

object Vent{
  def apply(/*position: Point2D*/): Vent = VentImpl(position)
}

case class VentImpl(/*override val position: Point2D*/) extends Vent {
  override def useVent(): Unit = ???
}