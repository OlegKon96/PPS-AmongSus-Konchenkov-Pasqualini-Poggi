package it.amongsus.core.entities.player

import java.awt.Color

import it.amongsus.core.entities.map.Collectionable
import it.amongsus.core.entities.player.Movement.{Down, Left, Right, Up}
import it.amongsus.core.entities.util.Point2D

trait Crewmate extends AlivePlayer{}

object Crewmate{
  def apply(clientId: String, username: String, position: Point2D): Crewmate =
    CrewmateImpl("green", clientId, username, position)
}

case class CrewmateImpl(override val color: String,
                        override val clientId: String,
                        override val username: String,
                        override val position: Point2D) extends Crewmate {

  override def move(direction: Movement): Unit = direction match {
    case Up() => Crewmate(clientId, username, Point2D(position.x, position.y + 1))
    case Down() => Crewmate(clientId, username, Point2D(position.x, position.y - 1))
    case Left() => Crewmate(clientId, username, Point2D(position.x - 1, position.y))
    case Right() => Crewmate(clientId, username, Point2D(position.x + 1, position.y))
  }

  override def vote(): Unit = ???

  override def report(): Unit = ???

  override def callEmergency(): Unit = ???

  private def collect(): Unit = Collectionable.apply(position)

}
