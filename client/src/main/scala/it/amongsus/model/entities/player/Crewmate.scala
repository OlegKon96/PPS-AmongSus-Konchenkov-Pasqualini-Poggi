package it.amongsus.model.entities.player

import it.amongsus.model.entities.map.Collectionable
import it.amongsus.model.entities.util.Point2D

trait Crewmate extends AlivePlayer{}

object Crewmate{
  def apply(position: Point2D): Crewmate = CrewmateImpl(position)
}

case class CrewmateImpl(override val position: Point2D) extends Crewmate{

  override def move(direction: Point2D): Unit = ???

  override def vote(): Unit = ???

  override def report(): Unit = ???

  override def callEmergency(): Unit = ???

  private def collect(): Unit = Collectionable.apply(position)

}
