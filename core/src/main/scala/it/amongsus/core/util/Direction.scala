package it.amongsus.core.util

/**
 * Trait that manages the movement in the game
 */
trait Direction

object Direction {
  /**
   * Tells that Player wants to move up
   */
  case object Up extends Direction
  /**
   * Tells that Player wants to move down
   */
  case object Down extends Direction
  /**
   * Tells that Player wants to move left
   */
  case object Left extends Direction
  /**
   * Tells that Player wants to move right
   */
  case object Right extends Direction
}