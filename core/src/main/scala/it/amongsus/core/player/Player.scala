package it.amongsus.core.player

import it.amongsus.core.util.MapHelper.GameMap
import it.amongsus.core.Entity
import it.amongsus.core.util.Direction

/**
 * Trait that represents the actions of the Game's Player.
 */
trait Player extends Entity[Player]{
  /**
   * Method that manages the move of the player in the game map.
   *
   * @param direction to move on.
   * @param map of the game.
   * @return player whit updated position, None otherwise.
   */
  def move(direction: Direction, map: GameMap) : Option[Player]
  /**
   * Username of the player.
   *
   * @return player's username.
   */
  def username: String
  /**
   * Id of the player
   *
   * @return player's client id.
   */
  def clientId: String
  /**
   * Range of view of the player
   *
   * @return player's field of view.
   */
  def fieldOfView: Int
}