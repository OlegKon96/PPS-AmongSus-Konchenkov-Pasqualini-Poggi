package it.amongsus.core.player

import it.amongsus.core.util.MapHelper.GameMap
import it.amongsus.core.player.CrewmateGhost.CrewmateGhostImpl
import it.amongsus.core.util.MovePlayer._
import it.amongsus.core.util.{Direction, Point2D}

/**
 * Trait that manages the Ghost of the Impostor of the game.
 */
trait ImpostorGhost extends Player with DeadPlayer with Impostor

object ImpostorGhost{
  def apply(color: String, clientId: String, username: String, position: Point2D): ImpostorGhost =
    ImpostorGhostImpl(color, clientId, username, Constants.ImpostorGhost.FIELD_OF_VIEW, position)

  def apply(impostorGhost: ImpostorGhost): ImpostorGhost = ImpostorGhostImpl(impostorGhost.color,
    impostorGhost.clientId, impostorGhost.username, impostorGhost.fieldOfView, impostorGhost.position)

  private case class ImpostorGhostImpl(override val color: String,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val position: Point2D) extends ImpostorGhost {

    override def move(direction: Direction, map: GameMap): Option[Player] = {
      movePlayer(ImpostorGhost(this), direction, map)
    }
  }
}