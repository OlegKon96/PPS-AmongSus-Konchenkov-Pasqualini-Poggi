package it.amongsus.core.player

import it.amongsus.core
import it.amongsus.core.Drawable
import it.amongsus.core.map.{DeadBody, Floor, Tile}
import it.amongsus.core.util.Point2D

/**
 * Trait that manages the Alive Player of the game
 */
trait AlivePlayer {
  self: Player =>
  /**
   * Field to check if the emergency was called previously or not
   *
   * @return
   */
  def emergencyCalled: Boolean
  /**
   * Method to call an emergency
   *
   * @param player that calls the emergency
   * @return
   */
  def callEmergency(player: Player): Player = {
    player match {
      case crewmateAlive: CrewmateAlive => core.player.CrewmateAlive(crewmateAlive.color, emergencyCalled = true,
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
  def canCallEmergency(player: AlivePlayer, emergencyButtons: Seq[Drawable[Tile]]): Boolean = {
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
  def checkCollision(pos: Point2D, map: Array[Array[Drawable[Tile]]]): Boolean = {
    map(pos.x)(pos.y) match {
      case _: Floor => false
      case _ => true
    }
  }
  /**
   * Position of the player
   *
   * @return
   */
  def position:Point2D = self.position
}