package it.amongsus.core.entities.player

import it.amongsus.core.entities.map.{Tile, Vent}
import it.amongsus.core.entities.util.Movement.{Down, Left, Right, Up}
import it.amongsus.core.entities.util.{Movement, Point2D}

/**
 * Trait that manages the Impostor of the game
 */
trait ImpostorAlive extends AlivePlayer with Impostor {
  /**
   * Manages the Impostar that killed a Crewmate
   */
  def canKill(position: Point2D, players: Seq[Player]): Boolean

  def kill(position: Point2D, players: Seq[Player]): Option[Player]

  /**
   * Manages the use of the vent
   */
  def useVent(vent: Seq[(Vent, Vent)]): Option[Player]

  def canVent(vent: Seq[(Vent, Vent)]): Option[Point2D]

}

object ImpostorAlive {
  def apply(clientId: String, username: String, position: Point2D): ImpostorAlive =
    ImpostorAliveImpl("green", clientId, username, Constants.Impostor.FIELD_OF_VIEW, position)

  private case class ImpostorAliveImpl(override val color: String,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val position: Point2D) extends ImpostorAlive {

    override def move(direction: Movement, map: Array[Array[Tile]]): Option[Player] = {
      val newPlayer = direction match {
        case Up() => ImpostorAlive(clientId, username, Point2D(position.x - 1, position.y))
        case Down() => ImpostorAlive(clientId, username, Point2D(position.x + 1, position.y))
        case Left() => ImpostorAlive(clientId, username, Point2D(position.x, position.y - 1))
        case Right() => ImpostorAlive(clientId, username, Point2D(position.x, position.y + 1))
      }
      if (checkCollision(newPlayer.position, map)) None else Option(newPlayer)
    }

    override def canKill(position: Point2D, players: Seq[Player]): Boolean = {
      players.exists(player =>
        player.isInstanceOf[CrewmateAlive] && player.position.distance(position) < Constants.Impostor.KILL_DISTANCE)
    }

    override def kill(position: Point2D, players: Seq[Player]): Option[Player] = {
      players.find(player =>
        player.isInstanceOf[CrewmateAlive] &&
          player.position.distance(position) < Constants.Impostor.KILL_DISTANCE) match {
        case Some(player) => Option(player)
        case None => None
      }
    }

    /**
     * Manages the use of the vent
     */
    override def useVent(vent: Seq[(Vent, Vent)]): Option[Player] = {
      canVent(vent) match {
        case Some(pos) => Option(ImpostorAlive(clientId, username, pos))
        case None => None
      }
    }

    override def canVent(vent: Seq[(Vent, Vent)]): Option[Point2D] = {
      var pos: Option[Point2D] = None
      vent.foreach(v => {
        if (v._1.position == position) {
          pos = Option(v._2.position)
        }
        else if (v._2.position == position) {
          pos = Option(v._1.position)
        }
      })
      pos
    }
  }

}
