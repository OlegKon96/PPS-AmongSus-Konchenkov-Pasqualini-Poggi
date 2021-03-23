package it.amongsus.core.player

import it.amongsus.core.Drawable
import it.amongsus.core.util.MapHelper.GameMap
import it.amongsus.core.map.Tile
import it.amongsus.core.util.MovePlayer._
import it.amongsus.core.util.{Direction, Point2D}

/**
 * Trait that manages the Impostor of the game.
 */
trait ImpostorAlive extends Player with AlivePlayer with Impostor {
  /**
   * Method that manages the Impostor that killed a Crewmate.
   *
   * @param players sequence of the players of the game.
   * @return true if there is a player in range, else otherwise.
   */
  def canKill(players: Seq[Player], checkKill : (Player, Player) => Boolean): Boolean
  /**
   * Method that manages the kill of the game.
   *
   * @param players sequence of the players of the game.
   * @return a player that is in range to kill, None otherwise.
   */
  def kill(players: Seq[Player], checkKill : (Player, Player) => Boolean): Option[Player]
  /**
   * Method that manages the use of the vent.
   *
   * @param vent sequence of the vent couple of the game.
   * @return a player whit modified position if he can vent, None otherwise.
   */
  def useVent(vent: Seq[(Drawable[Tile], Drawable[Tile])]): Option[Player]
  /**
   * Method that manages id player can use vent or not
   *
   * @param vent sequence of the vent couple of the game.
   * @return a new position if the player can vent, None otherwise.
   */
  def canVent(vent: Seq[(Drawable[Tile], Drawable[Tile])]): Option[Point2D]
}

object ImpostorAlive {
  def apply(color: String, emergencyCalled: Boolean, clientId: String, username: String, position: Point2D):
  ImpostorAlive = ImpostorAliveImpl(color, emergencyCalled, clientId, username,
    Constants.Impostor.FIELD_OF_VIEW, position)

  def apply(impostorAlive: ImpostorAlive): ImpostorAlive = ImpostorAliveImpl(impostorAlive.color,
    impostorAlive.emergencyCalled, impostorAlive.clientId, impostorAlive.username,
    impostorAlive.fieldOfView, impostorAlive.position)

  private case class ImpostorAliveImpl(override val color: String,
                                       override val emergencyCalled: Boolean,
                                       override val clientId: String,
                                       override val username: String,
                                       override val fieldOfView: Int,
                                       override val position: Point2D) extends ImpostorAlive {

    override def move(direction: Direction, map: GameMap): Option[Player] = {
      movePlayer(ImpostorAlive(this), direction, map)
    }

    override def canKill(players: Seq[Player], checkKill : (Player, Player) => Boolean): Boolean = {
      players.exists(player => checkKill(player, this))
    }

    override def kill(players: Seq[Player], checkKill : (Player, Player) => Boolean): Option[Player] = {
      players.find(player => checkKill(player, this)) match {
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