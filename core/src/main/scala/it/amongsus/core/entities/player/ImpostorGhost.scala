package it.amongsus.core.entities.player

import it.amongsus.core.entities.map.Tile
import it.amongsus.core.entities.util.Movement.{Down, Left, Right, Up}
import it.amongsus.core.entities.util.{Movement, Point2D}


/**
 * Trait that manages the Ghost of the Impostor of the game
 */
trait ImpostorGhost extends DeadPlayer with Impostor

object ImpostorGhost{
  def apply(clientId: String, username: String, position: Point2D): ImpostorGhost =
    ImpostorGhostImpl("green", clientId, username, Constants.Impostor.FIELD_OF_VIEW, position)

  private case class ImpostorGhostImpl(override val color: String,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val position: Point2D) extends ImpostorGhost {

    override def move(direction: Movement, map: Array[Array[Tile]]): Option[Player] = {
      val newPlayer = direction match {
        case Up() => ImpostorGhost(clientId, username, Point2D(position.x - 1, position.y))
        case Down() => ImpostorGhost(clientId,username, Point2D(position.x + 1, position.y))
        case Left() => ImpostorGhost(clientId, username, Point2D(position.x, position.y - 1))
        case Right() => ImpostorGhost(clientId, username, Point2D(position.x, position.y + 1))
      }
      if (checkCollision(newPlayer.position, map)) None else Option(newPlayer)
    }
  }
}