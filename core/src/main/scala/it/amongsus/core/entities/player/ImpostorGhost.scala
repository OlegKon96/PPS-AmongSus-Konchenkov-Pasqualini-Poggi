package it.amongsus.core.entities.player

import java.awt.Color

import it.amongsus.core.entities.player.Movement._
import it.amongsus.core.entities.util.Point2D

/**
 * Trait that manages the Ghost of the Impostor of the game
 */
trait ImpostorGhost extends DeadPlayer{
  /**
   * Manages the Impostor that can sabotage an item of the game
   */
  def sabotage():Unit
}

object ImpostorGhost{
  def apply(clientId: String, username: String, position: Point2D): ImpostorGhost =
    ImpostorGhostImpl("green", clientId, username, position)
}

case class ImpostorGhostImpl(override val color: String,
                             override val clientId: String,
                             override val username: String,
                             override val position: Point2D) extends ImpostorGhost {

  override def move(direction: Movement): Unit = direction match{
    case Up() => ImpostorGhost(clientId, username, Point2D(position.x, position.y + 1))
    case Down() => ImpostorGhost(clientId, username, Point2D(position.x, position.y - 1))
    case Left() => ImpostorGhost(clientId, username, Point2D(position.x - 1, position.y))
    case Right() => ImpostorGhost(clientId,username, Point2D(position.x + 1, position.y))
  }

  override def sabotage(): Unit = ???
}
