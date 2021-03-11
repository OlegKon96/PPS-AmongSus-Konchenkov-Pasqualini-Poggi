package it.amongsus.core.player

import it.amongsus.core.Drawable
import it.amongsus.core.map.{Tile, Vent}
import it.amongsus.core.util.MovePlayer._
import it.amongsus.core.util.{Movement, Point2D}

/**
 * Trait that manages the Impostor of the game
 */
trait ImpostorAlive extends Player with AlivePlayer with Impostor {
  /**
   * Method that manages the Impostor that killed a Crewmate
   *
   * @param position of the player
   * @param players sequence of the players of the game
   * @return boolean
   */
  def canKill(position: Point2D, players: Seq[Player]): Boolean
  /**
   * Method that manages the kill of the game
   *
   * @param position of the player
   * @param players sequence of the players of the game
   * @return boolean
   */
  def kill(position: Point2D, players: Seq[Player]): Option[Player]
  /**
   * Method that manages the use of the vent
   *
   * @param vent sequence of the vent couple of the game
   * @return
   */
  def useVent(vent: Seq[(Drawable[Tile], Drawable[Tile])]): Option[Player]
  /**
   * Method that manages id player can use vent or not
   *
   * @param vent sequence of the vent couple of the game
   * @return
   */
  def canVent(vent: Seq[(Drawable[Tile], Drawable[Tile])]): Option[Point2D]
}

object ImpostorAlive {
  def apply(color: String, emergencyCalled: Boolean, clientId: String, username: String, position: Point2D):
  ImpostorAlive = ImpostorAliveImpl(color, emergencyCalled, clientId, username,
    Constants.Impostor.FIELD_OF_VIEW, position)

  private case class ImpostorAliveImpl(override val color: String,
                                       override val emergencyCalled: Boolean,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val position: Point2D) extends ImpostorAlive {

    override def move(direction: Movement, map: Array[Array[Drawable[Tile]]]): Option[Player] = {
      movePlayer(ImpostorAlive(color, emergencyCalled, clientId, username, position), direction, map)
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

    override def useVent(vent: Seq[(Drawable[Tile], Drawable[Tile])]): Option[Player] = {
      canVent(vent) match {
        case Some(pos) => Option(ImpostorAlive(color, emergencyCalled, clientId, username, pos))
        case None => None
      }
    }

    override def canVent(vents: Seq[(Drawable[Tile], Drawable[Tile])]): Option[Point2D] = {
      var newPosition: Option[Point2D] = None
      vents.foreach(vent => {
        if (vent._1.position == position) {
          newPosition = Option(vent._2.position)
        }
        else if (vent._2.position == position) {
          newPosition = Option(vent._1.position)
        }
      })
      newPosition
    }
  }
}