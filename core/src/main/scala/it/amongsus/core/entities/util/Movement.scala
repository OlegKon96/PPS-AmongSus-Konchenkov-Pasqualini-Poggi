package it.amongsus.core.entities.util

/**
 * Trait that manages the movement in the game
 */
trait Movement

object Movement {
  /**
   * Tells that Player wants to move up
   */
  case class Up() extends Movement
  /**
   * Tells that Player wants to move down
   */
  case class Down() extends Movement
  /**
   * Tells that Player wants to move left
   */
  case class Left() extends Movement
  /**
   * Tells that Player wants to move right
   */
  case class Right() extends Movement
}