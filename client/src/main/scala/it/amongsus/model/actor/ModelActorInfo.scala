package it.amongsus.model.actor

import akka.actor.ActorRef
import it.amongsus.controller.TimerStatus
import it.amongsus.controller.actor.ControllerActorMessages._
import it.amongsus.core.map.MapHelper.{GameMap, generateEmergencyButtons, generateVentLinks}
import it.amongsus.core.map._
import it.amongsus.core.player.PlayerHelper.{checkPosition, emergencyDistance, reportDistance}
import it.amongsus.core.player._
import it.amongsus.core.util.ActionType.{EmergencyAction, KillAction, ReportAction, VentAction}
import it.amongsus.core.util.Direction
import it.amongsus.core.{Drawable, map}

trait ModelActorInfo {
  /**
   * Sequence of Players of the game.
   */
  var gamePlayers: Seq[Player]
  /**
   * Sequence of Coins of the game.
   */
  var gameCoins: Seq[Coin]
  /**
   * Sequence of a players' DeadBody.
   */
  var deadBodies: Seq[DeadBody]
  /**
   * Check if timer is running or not.
   */
  var isTimerOn: Boolean
  /**
   * The ID of the Client.
   * @return client id.
   */
  def clientId: String
  /**
   * The reference of the Game Server.
   *
   * @return controller actor ref.
   */
  def controllerRef: Option[ActorRef]
  /**
   * Method that finds my characters.
   *
   * @return my player.
   */
  def myCharacter: Player
  /**
   * Method that updates position of the characters.
   *
   * @param direction to move on.
   */
  def updateMyChar(direction: Direction): Unit
  /**
   * Method that updates buttons of the characters.
   *
   * @param player of the game to update.
   * @return a sequence of updated players.
   */
  def updatePlayer(player: Player): Seq[Player]
  /**
   * Method of the Impostor to use vent.
   */
  def useVent(): Unit
  /**
   * Method of an Alive player that allows him one time per game to call an emergency.
   */
  def callEmergency(): Unit
  /**
   * Method of the Impostor to kill a player.
   */
  def kill(): Unit
  /**
   * Check the status of the timer.
   *
   * @param status of the timer.
   */
  def checkTimer(status: TimerStatus): Unit
  /**
   * Method that kills a player during a vote session.
   *
   * @param username player username.
   */
  def killAfterVote(username: String): Unit
  /**
   * Method that remove a player.
   *
   * @param clientId of the player to remove.
   */
  def removePlayer(clientId: String): Unit
  /**
   * Method of the impostor that allows him to reduce crewmate field of view.
   *
   * @param state on/off.
   */
  def sabotage(state: Boolean): Unit


}

object ModelActorInfo {
  def apply(): ModelActorInfo = ModelActorInfoData(None, None, Seq(), Seq(), "", isTimerOn = false)

  def apply(controllerRef: Option[ActorRef], map: Option[GameMap],
            playersList: Seq[Player], gameCoins: Seq[Coin], clientId: String): ModelActorInfo =
    ModelActorInfoData(controllerRef, map, playersList, gameCoins, clientId, isTimerOn = false)
}

case class ModelActorInfoData(override val controllerRef: Option[ActorRef],
                              private val gameMap: Option[GameMap],
                              override var gamePlayers: Seq[Player],
                              override var gameCoins: Seq[Coin],
                              override val clientId: String,
                              override var isTimerOn: Boolean) extends ModelActorInfo {

  private val ventList: Seq[(Drawable[Tile], Drawable[Tile])] = gameMap match {
    case Some(map) => generateVentLinks(map)
    case None => Seq()
  }

  private val emergencyButtons: Seq[Drawable[Tile]] = gameMap match {
    case Some(map) => generateEmergencyButtons(map)
    case None => Seq()
  }

  override var deadBodies: Seq[DeadBody] = Seq()

  override def updateMyChar(direction: Direction): Unit = {
    myCharacter.move(direction, gameMap.get) match {
      case Some(player) =>
        playerUpdated(player match {
          case crewmate: Crewmate =>
            crewmate.canCollect(gameCoins, checkPosition) match {
              case Some(toCollect) =>
                gameCoins = gameCoins.filter(coin => coin != toCollect)
                crewmate.collect(crewmate)
              case None => crewmate
            }
          case _ => player
        })
      case None =>
    }
  }

  override def useVent(): Unit = {
    myCharacter match {
      case impostorAlive: ImpostorAlive => impostorAlive.useVent(ventList) match {
        case Some(p) => playerUpdated(p)
        case None =>
      }
      case _ =>
    }
  }

  override def callEmergency(): Unit = {
    myCharacter match {
      case alive: AlivePlayer => updatePlayer(alive.callEmergency(alive))
      case _ =>
    }
  }

  override def kill(): Unit = {
    myCharacter match {
      case impostorAlive: ImpostorAlive =>
        impostorAlive.kill(impostorAlive.position, gamePlayers) match {
          case Some(player) =>
            val dead = CrewmateGhost(player.color, player.clientId, player.username,
              player.asInstanceOf[CrewmateAlive].numCoins, player.position)
            deadBodies = deadBodies :+ map.DeadBody(player.color, dead.position)
            updatePlayer(dead)
            controllerRef.get ! UpdatedMyCharController(dead, gamePlayers, deadBodies)
            controllerRef.get ! UpdatedPlayersController(myCharacter, gamePlayers, gameCoins, deadBodies)
          case None =>
        }
      case _ =>
    }
  }

  override def updatePlayer(player: Player): Seq[Player] = {
    val index = gamePlayers.indexOf(gamePlayers.find(p => p.clientId == player.clientId).get)
    gamePlayers = gamePlayers.updated(index, player)

    myCharacter match {
      case alivePlayer: AlivePlayer =>
        if (alivePlayer.canCallEmergency(emergencyButtons, emergencyDistance)) {
          controllerRef.get ! ActionOnController(EmergencyAction)
        } else {
          controllerRef.get ! ActionOffController(EmergencyAction)
        }
        if (alivePlayer.canReport(deadBodies, reportDistance)) {
          controllerRef.get ! ActionOnController(ReportAction)
        } else {
          controllerRef.get ! ActionOffController(ReportAction)
        }
        alivePlayer match {
          case impostorAlive: ImpostorAlive =>
            impostorAlive.canVent(ventList) match {
              case Some(_) => controllerRef.get ! ActionOnController(VentAction)
              case None => controllerRef.get ! ActionOffController(VentAction)
            }
            if (impostorAlive.canKill(myCharacter.position, gamePlayers) && !isTimerOn) {
              controllerRef.get ! ActionOnController(KillAction)
            } else if (!isTimerOn) {
              controllerRef.get ! ActionOffController(KillAction)
            }
          case _ =>
        }
      case _ => controllerRef.get ! ActionOffController(ReportAction)
    }
    gamePlayers
  }

  override def myCharacter: Player = gamePlayers.find(player => player.clientId == this.clientId).get

  override def killAfterVote(username: String): Unit = {
    val player = gamePlayers.find(p => p.username == username).get
    gamePlayers = gamePlayers.updated(gamePlayers.indexOf(player), player match {
      case impostorAlive: ImpostorAlive => ImpostorGhost(impostorAlive.color, impostorAlive.clientId,
        impostorAlive.username, impostorAlive.position)
      case crewmateAlive: CrewmateAlive => CrewmateGhost(crewmateAlive.color, crewmateAlive.clientId,
        crewmateAlive.username, crewmateAlive.numCoins, crewmateAlive.position)
      case _ => player
    })
  }

  override def checkTimer(status: TimerStatus): Unit = myCharacter match {
    case _: ImpostorAlive => controllerRef.get ! KillTimerController(status)
    case _ =>
  }

  override def sabotage(state: Boolean): Unit = {
    val newPlayers = myCharacter match {
      case impostor: Impostor => impostor.sabotage(gamePlayers, state)
    }
    newPlayers.foreach(player => controllerRef.get ! UpdatedMyCharController(player, gamePlayers, deadBodies))
  }

  override def removePlayer(clientId: String): Unit = {
    gamePlayers = gamePlayers.filter(player => player.clientId != clientId)
    controllerRef.get ! UpdatedPlayersController(myCharacter, gamePlayers, gameCoins, deadBodies)
  }

  private def playerUpdated(player: Player): Unit = {
    updatePlayer(player)
    controllerRef.get ! UpdatedMyCharController(myCharacter, gamePlayers, deadBodies)
    controllerRef.get ! UpdatedPlayersController(myCharacter, gamePlayers, gameCoins, deadBodies)
  }
}