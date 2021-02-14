package it.amongsus.model.entities.player

/**
 * Trait that manages the Alive Player of the game
 */
trait AlivePlayer extends Player {
  def vote(): Unit
  def report(): Unit
  def callEmergency() : Unit
}
