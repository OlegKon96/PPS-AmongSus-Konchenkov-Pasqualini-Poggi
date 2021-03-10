package it.amongsus.core.entities.util

import it.amongsus.core.entities.Drawable
import it.amongsus.core.entities.map.Tile
import it.amongsus.core.entities.player.{CrewmateAlive, CrewmateGhost, ImpostorAlive, ImpostorGhost}
import scala.annotation.implicitNotFound

/**
 * Pimp my library on Point2D
 */
import it.amongsus.core.entities.util.RichPoint2D._

trait MovePlayer[A] {
  @implicitNotFound(s"No way to move a A." +
    s"An implicit MovePLayer[A] must be in scope")
  def move(player: A, direction: Movement, map: Array[Array[Drawable[Tile]]]): Option[A]
}

object MovePlayer {
  def movePlayer[A](player: A,
                    direction: Movement,
                    map: Array[Array[Drawable[Tile]]])(implicit movable: MovePlayer[A]): Option[A] = {
    movable.move(player, direction, map)
  }

  implicit object AliveCrewmateMovement extends MovePlayer[CrewmateAlive] {
    override def move(player: CrewmateAlive,
                      direction: Movement,
                      map: Array[Array[Drawable[Tile]]]): Option[CrewmateAlive] = {
      val newPlayer = CrewmateAlive(player.color, player.emergencyCalled, player.fieldOfView, player.clientId,
          player.username, player.numCoins, player.position.movePoint(direction))
      if (newPlayer.checkCollision(newPlayer.position, map)) None else Option(newPlayer)
    }
  }

  implicit object DeadCrewmatePlayerMovement extends MovePlayer[CrewmateGhost] {
    override def move(player: CrewmateGhost,
                      direction: Movement,
                      map: Array[Array[Drawable[Tile]]]): Option[CrewmateGhost] = {
      val newPlayer = CrewmateGhost(player.color, player.clientId, player.username, player.numCoins,
        player.position.movePoint(direction))
      if (newPlayer.checkCollision(newPlayer.position, map)) None else Option(newPlayer)
    }
  }

  implicit object AliveImpostorMovement extends MovePlayer[ImpostorAlive] {
    override def move(player: ImpostorAlive,
                      direction: Movement,
                      map: Array[Array[Drawable[Tile]]]): Option[ImpostorAlive] = {
      val newPlayer = ImpostorAlive(player.color, player.emergencyCalled, player.clientId, player.username,
        player.position.movePoint(direction))
      if (newPlayer.checkCollision(newPlayer.position, map)) None else Option(newPlayer)
    }
  }

  implicit object DeadImpostorPlayerMovement extends MovePlayer[ImpostorGhost] {
    override def move(player: ImpostorGhost,
                      direction: Movement,
                      map: Array[Array[Drawable[Tile]]]): Option[ImpostorGhost] = {
      val newPlayer = ImpostorGhost(player.color, player.clientId, player.username,
        player.position.movePoint(direction))
      if (newPlayer.checkCollision(newPlayer.position, map)) None else Option(newPlayer)
    }
  }
}
