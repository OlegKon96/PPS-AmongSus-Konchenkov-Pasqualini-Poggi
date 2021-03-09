package it.amongsus.core.entities.player

import it.amongsus.core.entities.map.{DeadBody, Emergency, Floor, Tile}
import it.amongsus.core.entities.util.Point2D

/**
 * Trait that manages the Alive Player of the game
 */
trait AlivePlayer {
  self: Player =>
  def emergencyCalled: Boolean

  def callEmergency(player: Player): Player = {
    player match {
      case crewmateAlive: CrewmateAlive => CrewmateAlive(crewmateAlive.color, emergencyCalled = true,
        crewmateAlive.clientId, crewmateAlive.username,crewmateAlive.numCoins, crewmateAlive.position)
      case impostorAlive: ImpostorAlive => ImpostorAlive(impostorAlive.color, emergencyCalled = true,
        impostorAlive.clientId, impostorAlive.username, impostorAlive.position)
    }
  }

  /**
   * Method to let player to report a dead body
   *
   * @param pos         position of the player
   * @param deadPlayers sequence of dead bodies of the game
   * @return boolean
   */
  def canReport(pos: Point2D, deadPlayers: Seq[DeadBody]): Boolean = {
    deadPlayers.exists(player => player.position.distance(pos) < Constants.REPORT_DISTANCE)
  }

  /**
   * Method to let player to call an emergency in the game
   *
   * @param player           game player
   * @param emergencyButtons sequence of emergency buttons of the game
   * @return boolean
   */
  def canCallEmergency(player: AlivePlayer, emergencyButtons: Seq[Emergency]): Boolean = {
    emergencyButtons.exists(button =>
      button.position.distance(player.position) < Constants.EMERGENCY_DISTANCE) && ! player.emergencyCalled
  }

  /**
   * Method to check collisions of the player
   *
   * @param pos position of the player
   * @param map game map
   * @return
   */
  def checkCollision(pos: Point2D, map: Array[Array[Tile]]): Boolean = {
    map(pos.x)(pos.y) match {
      case _: Floor => false
      case _ => true
    }
  }

  def position:Point2D = self.position
}

