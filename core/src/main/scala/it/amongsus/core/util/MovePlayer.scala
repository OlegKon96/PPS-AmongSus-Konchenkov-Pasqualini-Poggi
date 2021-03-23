package it.amongsus.core.util

import it.amongsus.core.util.MapHelper.GameMap
import it.amongsus.core.player.{CrewmateAlive, CrewmateGhost, ImpostorAlive, ImpostorGhost}

/**
 * Pimp my library on Point2D
 */
import it.amongsus.core.util.RichPoint2D._

trait MovePlayer[A] {
  /**
   * Updates a player's position.
   * @param player to move.
   * @param direction in which move the player.
   * @param gameMap game map.
   * @return a new instance of the player whit modified position, None otherwise.
   */
  def move(player: A, direction: Direction, gameMap: GameMap): Option[A]
}

object MovePlayer {
  /**
   * Updates a player's position.
   * @param player to move.
   * @param direction in which move the player.
   * @param map game map.
   * @param movable implicit object.
   * @tparam A Player type.
   * @return a new instance of the player whit modified position, None otherwise.
   */
  def movePlayer[A](player: A,
                    direction: Direction,
                    map: GameMap)(implicit movable: MovePlayer[A]): Option[A] = {
    movable.move(player, direction, map)
  }

  implicit object AliveCrewmateMovement extends MovePlayer[CrewmateAlive] {
    override def move(player: CrewmateAlive,
                      direction: Direction,
                      gameMap: GameMap): Option[CrewmateAlive] = {
      val newPlayer = CrewmateAlive(player.color, player.emergencyCalled, player.fieldOfView, player.clientId,
          player.username, player.numCoins, player.position.movePoint(direction))
      if (newPlayer.checkCollision(gameMap)) None else Option(newPlayer)
    }
  }

  implicit object DeadCrewmatePlayerMovement extends MovePlayer[CrewmateGhost] {
    override def move(player: CrewmateGhost,
                      direction: Direction,
                      gameMap: GameMap): Option[CrewmateGhost] = {
      val newPlayer = CrewmateGhost(player.color, player.clientId, player.username, player.numCoins,
        player.position.movePoint(direction))
      if (newPlayer.checkCollision(gameMap)) None else Option(newPlayer)
    }
  }

  implicit object AliveImpostorMovement extends MovePlayer[ImpostorAlive] {
    override def move(player: ImpostorAlive,
                      direction: Direction,
                      gameMap: GameMap): Option[ImpostorAlive] = {
      val newPlayer = ImpostorAlive(player.color, player.emergencyCalled, player.clientId, player.username,
        player.position.movePoint(direction))
      if (newPlayer.checkCollision(gameMap)) None else Option(newPlayer)
    }
  }

  implicit object DeadImpostorPlayerMovement extends MovePlayer[ImpostorGhost] {
    override def move(player: ImpostorGhost,
                      direction: Direction,
                      gameMap: GameMap): Option[ImpostorGhost] = {
      val newPlayer = ImpostorGhost(player.color, player.clientId, player.username,
        player.position.movePoint(direction))
      if (newPlayer.checkCollision(gameMap)) None else Option(newPlayer)
    }
  }
}