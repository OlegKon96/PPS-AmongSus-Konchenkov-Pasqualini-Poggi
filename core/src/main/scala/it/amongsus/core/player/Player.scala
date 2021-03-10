package it.amongsus.core.player

import it.amongsus.core.{Drawable, Entity}
import it.amongsus.core.map.Tile
import it.amongsus.core.util.Movement

/**
 * Trait that represents the actions of the Game's Player
 */
trait Player extends Entity[Player]{
  /**
   * Method that manages the move of the player in the game map
   *
   * @param direction to move on
   * @param map of the game
   * @return
   */
  def move(direction: Movement, map: Array[Array[Drawable[Tile]]]) : Option[Player]
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