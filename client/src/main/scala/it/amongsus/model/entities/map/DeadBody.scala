package it.amongsus.model.entities.map

import it.amongsus.model.entities.Entity
import it.amongsus.model.entities.util.Point2D

trait DeadBody extends Entity{
  def report() : Unit
}

object DeadBody{
  def apply(position: Point2D): DeadBody = DeadBodyImpl(position)
}

case class DeadBodyImpl(override val position: Point2D) extends DeadBody {
  override def report(): Unit = ???
}