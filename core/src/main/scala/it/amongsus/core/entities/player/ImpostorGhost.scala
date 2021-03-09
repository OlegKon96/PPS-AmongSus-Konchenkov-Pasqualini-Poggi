package it.amongsus.core.entities.player

import it.amongsus.core.entities.Drawable
import it.amongsus.core.entities.map.Tile
import it.amongsus.core.entities.util.Movement._
import it.amongsus.core.entities.util.{Movement, Point2D}

/**
 * Trait that manages the Ghost of the Impostor of the game
 */
trait ImpostorGhost extends Player with DeadPlayer with Impostor

object ImpostorGhost{
  def apply(color: String, clientId: String, username: String, position: Point2D): ImpostorGhost =
    ImpostorGhostImpl(color, clientId, username, Constants.Impostor.FIELD_OF_VIEW, position)

  private case class ImpostorGhostImpl(override val color: String,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val position: Point2D) extends ImpostorGhost {

    override def move(direction: Movement, map: Array[Array[Drawable[Tile]]]): Option[Player] = {
      val newPlayer = direction match {
        case Up() => ImpostorGhost(color, clientId, username, Point2D(position.x - 1, position.y))
        case Down() => ImpostorGhost(color, clientId,username, Point2D(position.x + 1, position.y))
        case Left() => ImpostorGhost(color, clientId, username, Point2D(position.x, position.y - 1))
        case Right() => ImpostorGhost(color, clientId, username, Point2D(position.x, position.y + 1))
      }
      if (checkCollision(newPlayer.position, map)) None else Option(newPlayer)
    }
  }
}