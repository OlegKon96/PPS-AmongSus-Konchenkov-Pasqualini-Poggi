package it.amongsus.core.entities.player

import it.amongsus.core.entities.map.Tile
import it.amongsus.core.entities.util.Movement._
import it.amongsus.core.entities.util.{Movement, Point2D}

/**
 * Trait that manages the Crewmate Alive
 */
trait CrewmateAlive extends AlivePlayer with Crewmate

object CrewmateAlive {
  def apply(color: String, emergencyCalled: Boolean, clientId: String, username: String,
            numCoins: Int, position: Point2D): CrewmateAlive =
    CrewmateAliveImpl(color, emergencyCalled, clientId, username,
      Constants.Crewmate.FIELD_OF_VIEW, numCoins, position)

  def apply(color: String, emergencyCalled: Boolean, fieldOfView: Int, clientId: String, username: String,
            numCoins: Int, position: Point2D): CrewmateAlive =
    CrewmateAliveImpl(color, emergencyCalled, clientId, username,
      fieldOfView, numCoins, position)

  private case class CrewmateAliveImpl(override val color: String,
                                       override val emergencyCalled: Boolean,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val numCoins: Int,
                                       override val position: Point2D) extends CrewmateAlive {

    override def move(direction: Movement, map: Array[Array[Tile]]): Option[Player] = {
      val newPlayer = direction match {
        case Up() => CrewmateAlive(color, emergencyCalled, fieldOfView, clientId, username, numCoins,
          Point2D(position.x - 1, position.y))
        case Down() => CrewmateAlive(color, emergencyCalled, fieldOfView, clientId, username, numCoins,
          Point2D(position.x + 1, position.y))
        case Left() => CrewmateAlive(color, emergencyCalled, fieldOfView, clientId, username, numCoins,
          Point2D(position.x, position.y - 1))
        case Right() => CrewmateAlive(color, emergencyCalled, fieldOfView, clientId, username, numCoins,
          Point2D(position.x, position.y + 1))
      }
      if (checkCollision(newPlayer.position, map)) None else Option(newPlayer)
    }
  }
}