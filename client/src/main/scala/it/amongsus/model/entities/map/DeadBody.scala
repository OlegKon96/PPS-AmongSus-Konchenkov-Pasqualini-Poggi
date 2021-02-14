package it.amongsus.model.entities.map

trait DeadBody /*extends Entity*/{
  def report() : Unit
}

object DeadBody{
  def apply(/*position: Point2D*/): DeadBody = DeadBodyImpl(/*position*/)
}

case class DeadBodyImpl(/*override val position: Point2D*/) extends DeadBody {
  override def report(): Unit = ???
}