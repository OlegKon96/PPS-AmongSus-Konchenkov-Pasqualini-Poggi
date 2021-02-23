package it.amongsus.core.entities.player

import it.amongsus.core.entities.Entity
import it.amongsus.core.entities.map.Tile
import it.amongsus.core.entities.util.Movement

/**
 * Trait that represents the actions of the Game's Player
 */
trait Player extends Entity{
  /**
   * Method that manages the move of the player in the game map
   *
   * @param direction to move on
   * @param map of the game
   * @return
   */
  def move(direction: Movement, map: Array[Array[Tile]]) : Option[Player]
  /**
   * Username of the player
   *
   * @return
   */
  def username: String
  /**
   * Id of the player
   *
   * @return
   */
  def clientId: String
  /**
   * Range of view of the player
   *
   * @return
   */
  def fieldOfView: Int
}