package it.amongsus.model.entities.player

import it.amongsus.model.entities.Entity

/**
 * Trait that represents the actions of the Game's Player
 */
trait Player extends Entity{
  def move(direction: Movement) : Unit
}
