package it.amongsus.core.entities.player

import it.amongsus.core.entities.map.Tile
import it.amongsus.core.entities.util.Movement._
import it.amongsus.core.entities.util.{Movement, Point2D}

/**
 * Method to manages the Crewmate ghost
 */
trait CrewmateGhost extends DeadPlayer with Crewmate

object CrewmateGhost {
  def apply(clientId: String, username: String, numCoins: Int, position: Point2D): CrewmateGhost =
    CrewmateGhostImpl("green", clientId, username, Constants.Crewmate.FIELD_OF_VIEW,
      numCoins, position)

  private case class CrewmateGhostImpl(override val color: String,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val numCoins: Int,
                                       override val position: Point2D) extends CrewmateGhost {

    override def move(direction: Movement, map: Array[Array[Tile]]): Option[Player] = {
      val newPlayer = direction match {
        case Up() => CrewmateGhost(clientId, username, numCoins, Point2D(position.x - 1, position.y))
        case Down() => CrewmateGhost(clientId, username, numCoins, Point2D(position.x + 1, position.y))
        case Left() => CrewmateGhost(clientId, username, numCoins, Point2D(position.x, position.y - 1))
        case Right() => CrewmateGhost(clientId, username, numCoins, Point2D(position.x, position.y + 1))
      }
      if (checkCollision(newPlayer.position, map)) None else Option(newPlayer)
    }
  }
}