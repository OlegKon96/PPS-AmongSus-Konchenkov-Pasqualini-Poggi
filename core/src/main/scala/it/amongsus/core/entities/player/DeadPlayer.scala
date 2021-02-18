package it.amongsus.core.entities.player

import it.amongsus.core.entities.map.{Boundary, Tile}
import it.amongsus.core.entities.util.Point2D

trait DeadPlayer extends Player{
  def checkCollision(pos: Point2D, map: Array[Array[Tile]]): Boolean = {
    map(pos.x)(pos.y) match {
      case _: Boundary => true
      case _ => false
    }
  }
}


