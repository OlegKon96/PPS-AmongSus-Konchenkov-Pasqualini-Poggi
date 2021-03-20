package it.amongsus.core.player

import it.amongsus.core.util.MapHelper.GameMap
import it.amongsus.core.map.Boundary

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
  def checkCollision(map: GameMap): Boolean = {
    map(self.position.x)(self.position.y) match {
      case _: Boundary => true
      case _ => false
    }
  }
}