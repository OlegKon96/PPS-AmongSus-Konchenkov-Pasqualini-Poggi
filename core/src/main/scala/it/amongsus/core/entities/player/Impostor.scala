package it.amongsus.core.entities.player

/**
 * Trait that manages the Impostor
 */
trait Impostor extends Player{
  /**
   * Manages the sabotage of the game
   */
  def sabotage(): Unit = {}
}