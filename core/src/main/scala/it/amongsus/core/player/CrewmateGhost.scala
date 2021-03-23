package it.amongsus.core.player

import it.amongsus.core.util.MapHelper.GameMap
import it.amongsus.core.util.MovePlayer._
import it.amongsus.core.util.{Direction, Point2D}

/**
 * Method to manages the Crewmate ghost
 */
trait CrewmateGhost extends Player with DeadPlayer with Crewmate

object CrewmateGhost {
  def apply(color: String, clientId: String, username: String, numCoins: Int, position: Point2D): CrewmateGhost =
    CrewmateGhostImpl(color, clientId, username, Constants.CrewmateGhost.FIELD_OF_VIEW,
      numCoins, position)

  def apply(crewmateGhost: CrewmateGhost): CrewmateGhost = CrewmateGhostImpl(crewmateGhost.color,
    crewmateGhost.clientId, crewmateGhost.username, crewmateGhost.fieldOfView, crewmateGhost.numCoins,
    crewmateGhost.position)

  private case class CrewmateGhostImpl(override val color: String,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val numCoins: Int,
                                       override val position: Point2D) extends CrewmateGhost {

    override def move(direction: Direction, map: GameMap): Option[Player] = {
      movePlayer(CrewmateGhost(this), direction, map)
    }
  }
}