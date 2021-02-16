package it.amongsus.core.entities.map

import it.amongsus.core.entities.Entity
import it.amongsus.core.entities.util.Point2D

trait DeadBody extends Entity{
  def report() : Unit
}

object DeadBody{
  def apply(position: Point2D): DeadBody = DeadBodyImpl("green", position)
}

case class DeadBodyImpl(override val color: String, override val position: Point2D) extends DeadBody {
  override def report(): Unit = ???
}