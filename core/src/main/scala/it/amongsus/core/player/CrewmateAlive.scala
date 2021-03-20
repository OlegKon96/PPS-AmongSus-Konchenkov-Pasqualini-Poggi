package it.amongsus.core.player

import it.amongsus.core.map.MapHelper.GameMap
import it.amongsus.core.util.MovePlayer.movePlayer
import it.amongsus.core.util.{Direction, Point2D}

/**
 * Trait that manages the Crewmate Alive
 */
trait CrewmateAlive extends Player with AlivePlayer with Crewmate

object CrewmateAlive {
  def apply(color: String, emergencyCalled: Boolean, clientId: String, username: String,
            numCoins: Int, position: Point2D): CrewmateAlive =
    CrewmateAliveImpl(color, emergencyCalled, clientId, username,
      Constants.Crewmate.FIELD_OF_VIEW, numCoins, position)

  def apply(color: String, emergencyCalled: Boolean, fieldOfView: Int, clientId: String, username: String,
            numCoins: Int, position: Point2D): CrewmateAlive =
    CrewmateAliveImpl(color, emergencyCalled, clientId, username,
      fieldOfView, numCoins, position)

  def apply(crewmateAlive: CrewmateAlive): CrewmateAlive = CrewmateAliveImpl(crewmateAlive.color,
    crewmateAlive.emergencyCalled, crewmateAlive.clientId, crewmateAlive.username,
    crewmateAlive.fieldOfView, crewmateAlive.numCoins, crewmateAlive.position)

  private case class CrewmateAliveImpl(override val color: String,
                                       override val emergencyCalled: Boolean,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val numCoins: Int,
                                       override val position: Point2D) extends CrewmateAlive {

    override def move(direction: Direction, map: GameMap): Option[Player] = {
      movePlayer(CrewmateAlive(this), direction, map)
    }
  }

}