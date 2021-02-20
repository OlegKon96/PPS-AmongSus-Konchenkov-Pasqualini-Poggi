package it.amongsus.core.entities.player

import it.amongsus.core.entities.map.{Boundary, Tile}
import it.amongsus.core.entities.util.Point2D

/**
 * Method to manages the Dead Players
 */
trait DeadPlayer extends Player{
  /**
   * Method to check collision with the border of the GUI
   *
   * @param pos position of the player
   * @param map game map
   * @return boolean
   */
  def checkCollision(pos: Point2D, map: Array[Array[Tile]]): Boolean = {
    map(pos.x)(pos.y) match {
      case tile : Boundary => true
      case _ => false
    }
  }
}