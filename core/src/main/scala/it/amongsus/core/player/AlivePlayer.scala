package it.amongsus.core.player

import it.amongsus.core
import it.amongsus.core.Drawable
import it.amongsus.core.map.MapHelper.GameMap
import it.amongsus.core.map.{DeadBody, Floor, Tile}
import it.amongsus.core.util.Point2D

/**
 * Trait that manages the Alive Player of the game.
 */
trait AlivePlayer {
  self: Player =>
  /**
   * Field to check if the emergency was called previously or not.
   *
   * @return true if the player already called the emergency, false otherwise.
   */
  def emergencyCalled: Boolean
  /**
   * Method to call an emergency.
   *
   * @param player that calls the emergency.
   * @return a new instance of the same player but he will no longer be able to call an emergency.
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
   * @param position         position of the player.
   * @param deadPlayers sequence of dead bodies of the game.
   * @return true if the player is near enought to a dead body, else otherwise.
   */
  def canReport(position: Point2D, deadPlayers: Seq[DeadBody]): Boolean = {
    deadPlayers.exists(player => player.position.distance(position) < Constants.REPORT_DISTANCE)
  }
  /**
   * Method to let player to call an emergency in the game.
   *
   * @param player           game player.
   * @param emergencyButtons sequence of emergency buttons of the game.
   * @return true if the player if in the correct distance whit the emergency button else otherwise.
   */
  def canCallEmergency(player: AlivePlayer, emergencyButtons: Seq[Drawable[Tile]]): Boolean = {
    emergencyButtons.exists(button =>
      button.position.distance(player.position) < Constants.EMERGENCY_DISTANCE) && ! player.emergencyCalled
  }
  /**
   * Method to check collisions of the player.
   *
   * @param pos position of the player.
   * @param map game map.
   * @return true if collides, false otherwise.
   */
  def checkCollision(pos: Point2D, map: GameMap): Boolean = {
    map(pos.x)(pos.y) match {
      case _: Floor => false
      case _ => true
    }
  }
  /**
   * Position of the player.
   *
   * @return player's position.
   */
  def position:Point2D = self.position
}