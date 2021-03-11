package it.amongsus.core.player

import it.amongsus.core.Drawable
import it.amongsus.core.map.Tile
import it.amongsus.core.util.MovePlayer.movePlayer
import it.amongsus.core.util.{Movement, Point2D}


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

  private case class CrewmateAliveImpl(override val color: String,
                                       override val emergencyCalled: Boolean,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val numCoins: Int,
                                       override val position: Point2D) extends CrewmateAlive {

    override def move(direction: Movement, map: Array[Array[Drawable[Tile]]]): Option[Player] = {
      movePlayer(CrewmateAlive(color, emergencyCalled, fieldOfView, clientId, username, numCoins, position),
        direction, map)
    }
  }
}