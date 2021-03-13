package it.amongsus.core.player

import it.amongsus.core.Drawable
import it.amongsus.core.map.{Boundary, Tile}
import it.amongsus.core.util.Point2D

/**
 * Method to manages the Dead Players
 */.
trait DeadPlayer {
  self: Player =>
  /**
   * Method to check collision with the border of the GUI.
   *
   * @param pos position of the player.
   * @param map game map.
   * @return true if collides whit a boundary, false otherwise.
   */
  def checkCollision(pos: Point2D, map: Array[Array[Drawable[Tile]]]): Boolean = {
    map(pos.x)(pos.y) match {
      case _: Boundary => true
      case _ => false
    }
  }
}