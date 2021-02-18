package it.amongsus.core.entities.player

import it.amongsus.core.entities.Entity
import it.amongsus.core.entities.map.Tile
import it.amongsus.core.entities.util.Movement

/**
 * Trait that represents the actions of the Game's Player
 */
trait Player extends Entity{
  def move(direction: Movement, map: Array[Array[Tile]]) : Option[Player]
  def username: String
  def clientId: String
  def fieldOfView: Int
}