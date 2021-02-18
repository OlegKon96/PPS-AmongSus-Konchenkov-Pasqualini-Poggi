package it.amongsus.core.entities.player

import it.amongsus.core.entities.map.{DeadBody, Emergency, Floor, Tile}
import it.amongsus.core.entities.util.Point2D

/**
 * Trait that manages the Alive Player of the game
 */
trait AlivePlayer extends Player {
  def vote(): Unit = {

  }
  def canReport(pos: Point2D, deadPlayers: Seq[DeadBody]): Boolean = {
    deadPlayers.exists(player => player.position.distance(pos) < Constants.REPORT_DISTANCE)
  }
  def canCallEmergency(pos: Point2D, emergencyButtons: Seq[Emergency]): Boolean = {
    emergencyButtons.exists(button => button.position.distance(pos) < Constants.EMERGENCY_DISTANCE)
  }

  def checkCollision(pos: Point2D, map: Array[Array[Tile]]): Boolean = {
    map(pos.x)(pos.y) match {
      case _: Floor => false
      case _ => true
    }
  }
}

