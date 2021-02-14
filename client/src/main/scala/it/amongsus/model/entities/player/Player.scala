package it.amongsus.model.entities.player

import it.amongsus.model.entities.Entity
import it.amongsus.model.entities.util.Point2D

/**
 * Trait that represents the actions of the Game's Player
 */
trait Player extends Entity{
  def move(direction: Point2D) : Unit
}
