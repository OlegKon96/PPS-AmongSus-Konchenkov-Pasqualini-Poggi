package it.amongsus.core.entities.player

import java.awt.Color

import it.amongsus.core.entities.map.Collectionable
import it.amongsus.core.entities.player.Movement.{Down, Left, Right, Up}
import it.amongsus.core.entities.util.Point2D

trait CrewmateGhost extends DeadPlayer

object CrewmateGhost{
  def apply(clientId: String, username: String, position: Point2D): CrewmateGhost =
    CrewmateGhostImpl(Color.GREEN, clientId, username, position)}

case class CrewmateGhostImpl(override val color: Color,
                             override val clientId: String,
                             override val username: String,
                             override val position: Point2D) extends CrewmateGhost {

  override def move(direction: Movement): Unit = direction match{
    case Up() => CrewmateGhost(clientId, username, Point2D(position.x, position.y + 1))
    case Down() => CrewmateGhost(clientId, username, Point2D(position.x, position.y - 1))
    case Left() => CrewmateGhost(clientId, username, Point2D(position.x - 1, position.y))
    case Right() => CrewmateGhost(clientId,username, Point2D(position.x + 1, position.y))
  }

  private def collect(): Unit = Collectionable.apply(position)
}
