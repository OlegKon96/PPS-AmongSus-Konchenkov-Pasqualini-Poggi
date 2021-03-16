package it.amongsus.core.player

import it.amongsus.core.map.MapHelper.GameMap
import it.amongsus.core.map.Boundary
import it.amongsus.core.util.Point2D

/**
 * Method to manages the Dead Players
 */
trait DeadPlayer {
  self: Player =>
  /**
   * Method to check collision with the border of the GUI.
   *
   * @param position position of the player.
   * @param map game map.
   * @return true if collides whit a boundary, false otherwise.
   */
  def checkCollision(position: Point2D, map: GameMap): Boolean = {
    map(position.x)(position.y) match {
      case _: Boundary => true
      case _ => false
    }
  }
}