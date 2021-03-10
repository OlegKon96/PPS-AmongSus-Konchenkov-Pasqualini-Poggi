package it.amongsus.core.player

import it.amongsus.core.Drawable
import it.amongsus.core.util.MovePlayer._
import it.amongsus.core.util.{Movement, Point2D}
import it.amongsus.core.map.Tile

/**
 * Method to manages the Crewmate ghost
 */
trait CrewmateGhost extends Player with DeadPlayer with Crewmate

object CrewmateGhost {
  def apply(color: String, clientId: String, username: String, numCoins: Int, position: Point2D): CrewmateGhost =
    CrewmateGhostImpl(color, clientId, username, Constants.CrewmateGhost.FIELD_OF_VIEW,
      numCoins, position)

  private case class CrewmateGhostImpl(override val color: String,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val numCoins: Int,
                                       override val position: Point2D) extends CrewmateGhost {

    override def move(direction: Movement, map: Array[Array[Drawable[Tile]]]): Option[Player] = {
      movePlayer(CrewmateGhost(color, clientId, username, numCoins, position), direction, map)
    }
  }
}