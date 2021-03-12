package it.amongsus.core.util

/**
 * Trait that manages the movement in the game
 */
trait Direction

object Direction {
  /**
   * Tells that Player wants to move up
   */
  case class Up() extends Direction
  /**
   * Tells that Player wants to move down
   */
  case class Down() extends Direction
  /**
   * Tells that Player wants to move left
   */
  case class Left() extends Direction
  /**
   * Tells that Player wants to move right
   */
  case class Right() extends Direction
}