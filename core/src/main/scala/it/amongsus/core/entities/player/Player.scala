package it.amongsus.core.entities.player

import it.amongsus.core.entities.Entity
import it.amongsus.core.entities.util.Point2D

/**
 * Trait that represents the actions of the Game's Player
 */
trait Player extends Entity{
  def move(direction: Point2D) : Unit
}
