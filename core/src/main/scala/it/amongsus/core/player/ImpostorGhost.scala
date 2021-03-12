package it.amongsus.core.player

import it.amongsus.core.Drawable
import it.amongsus.core.util.MovePlayer._
import it.amongsus.core.map.Tile
import it.amongsus.core.util.{Direction, Point2D}

/**
 * Trait that manages the Ghost of the Impostor of the game
 */
trait ImpostorGhost extends Player with DeadPlayer with Impostor

object ImpostorGhost{
  def apply(color: String, clientId: String, username: String, position: Point2D): ImpostorGhost =
    ImpostorGhostImpl(color, clientId, username, Constants.ImpostorGhost.FIELD_OF_VIEW, position)

  private case class ImpostorGhostImpl(override val color: String,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val position: Point2D) extends ImpostorGhost {

    override def move(direction: Direction, map: Array[Array[Drawable[Tile]]]): Option[Player] = {
      movePlayer(ImpostorGhost(color, clientId, username, position), direction, map)
    }
  }
}