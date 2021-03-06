package it.amongsus.model.actor

import akka.actor.ActorRef
import it.amongsus.controller.TimerStatus
import it.amongsus.controller.actor.ControllerActorMessages._
import it.amongsus.core.entities.map._
import it.amongsus.core.entities.player._
import it.amongsus.core.entities.util.ButtonType.{EmergencyButton, KillButton, ReportButton, VentButton}
import it.amongsus.core.entities.util.{Movement, Point2D}
import scala.Array.ofDim
import scala.util.Random

trait ModelActorInfo {
  /**
   * Sequence of Players of the game
   */
  var gamePlayers: Seq[Player]
  /**
   * Sequence of Collectionables of the game
   */
  var gameCollectionables: Seq[Collectionable]
  /**
   * Sequence of a players' DeadBody
   */
  var deadBodys: Seq[DeadBody]
  /**
   * Check if timer is running or not
   */
  var isTimerOn: Boolean
  /**
   * The ID of the Client
   */
  def clientId: String
  /**
   * The reference of the Game Server
   *
   * @return
   */
  def controllerRef: Option[ActorRef]
  /**
   * The map of the game
   *
   * @return
   */
  def gameMap: Option[Array[Array[Tile]]]
  /**
   * Method that generates the map of the game
   *
   * @param map of the game
   * @return
   */
  def generateMap(map: Iterator[String]): Array[Array[Tile]]
  /**
   * Method that generates the collectionables of the game
   *
   * @param map of the game
   */
  def generateCollectionables(map: Array[Array[Tile]]): Unit
  /**
   * Method that finds my characters
   *
   * @return
   */
  def myCharacter: Player
  /**
   * Method that updates position of the characters
   *
   * @param direction to move on
   */
  def updateMyChar(direction: Movement): Unit
  /**
   * Method that updates buttons of the characters
   *
   * @param player of the game to update
   * @return
   */
  def updatePlayer(player: Player): Seq[Player]
  /**
   * Method of the Impostor to use vent
   */
  def useVent(): Unit
  /**
   * Method of an Alive player that allows him one time per game to call an emergency
   */
  def callEmergency(): Unit
  /**
   * Method of the Impostor to kill a player
   */
  def kill(): Unit
  /**
   * Check the status of the timer
   *
   * @param status of the timer
   */
  def checkTimer(status: TimerStatus): Unit
  /**
   * Method that kills a player during a vote session
   */
  def killAfterVote(username: String): Unit
  /**
   * Method that remove a player
   *
   * @param clientId of the player to remove
   */
  def removePlayer(clientId: String): Unit
  /**
   * Method of the impostor that allows him to reduce crewmate field of view
   */
  def sabotage(): Unit
  /**
   * Method of the impostor that allows him to reduce crewmate field of view
   */
  def sabotageOff(): Unit
}

object ModelActorInfo {
  def apply(): ModelActorInfo = ModelActorInfoData(None, None, Seq(), Seq(), "", isTimerOn = false)

  def apply(controllerRef: Option[ActorRef], map: Option[Array[Array[Tile]]],
            playersList: Seq[Player], gameCollectionables: Seq[Collectionable], clientId: String): ModelActorInfo =
    ModelActorInfoData(controllerRef, map, playersList, gameCollectionables, clientId, isTimerOn = false)
}

case class ModelActorInfoData(override val controllerRef: Option[ActorRef],
                              override val gameMap: Option[Array[Array[Tile]]],
                              override var gamePlayers: Seq[Player],
                              override var gameCollectionables: Seq[Collectionable],
                              override val clientId: String,
                              override var isTimerOn: Boolean) extends ModelActorInfo {

  val ventList: Seq[(Vent, Vent)] = generateVentLinks()
  val emergencyButtons: Seq[Emergency] = generateEmergencyButtons()
  override var deadBodys: Seq[DeadBody] = Seq()

  override def generateMap(map: Iterator[String]): Array[Array[Tile]] = {
    var j = 0
    var k = 0
    val n1 = 50
    val n2 = 72
    val tileMatrix = ofDim[Tile](n1, n2)

    map.foreach(line => {
      line.split(",").map(_.trim).foreach(col => {
        col match {
          case "189" => tileMatrix(j)(k) = Wall(Point2D(j, k))
          case "0" => tileMatrix(j)(k) = Other(Point2D(j, k))
          case "40" => tileMatrix(j)(k) = Floor(Point2D(j, k))
          case "1" => tileMatrix(j)(k) = Boundary(Point2D(j, k))
          case "66" => tileMatrix(j)(k) = Vent(Point2D(j, k))
          case "222" => tileMatrix(j)(k) = Emergency(Point2D(j, k))
        }
        k = k + 1
      })
      j = j + 1
      k = 0
    })
    generateCollectionables(tileMatrix)
    tileMatrix
  }

  override def generateCollectionables(map: Array[Array[Tile]]): Unit = {
    var tiles: Seq[Tile] = Seq.empty
    map.foreach(x => x.foreach {
      case tile: Floor => tiles = tiles :+ tile
      case _: Wall =>
      case _: Other =>
    })

    for (_ <- 0 until 10) {
      val rand = Random.nextInt(tiles.length)
      this.gameCollectionables = gameCollectionables :+ Collectionable(tiles(rand).position)
      tiles = tiles.take(rand) ++ tiles.drop(rand + 1)
    }
  }

  override def updateMyChar(direction: Movement): Unit = {
    myCharacter.move(direction, gameMap.get) match {
      case Some(player) =>
        playerUpdated(player match {
          case crew: Crewmate =>
            crew.canCollect(gameCollectionables, crew) match {
              case Some(collect) =>
                gameCollectionables = gameCollectionables.filter(c => c != collect)
                crew.collect(crew)
              case None => crew
            }
          case _ => player
        })
      case None =>
    }
  }

  override def useVent(): Unit = {
    myCharacter match {
      case p: ImpostorAlive => p.useVent(ventList) match {
        case Some(p) =>
          playerUpdated(p)
        case None =>
      }
      case _ =>
    }
  }

  private def playerUpdated(player: Player): Unit = {
    updatePlayer(player)
    controllerRef.get ! UpdatedMyCharController(myCharacter, gamePlayers, deadBodys)
    controllerRef.get ! UpdatedPlayersController(myCharacter, gamePlayers, gameCollectionables, deadBodys)
  }

  override def updatePlayer(player: Player): Seq[Player] = {
    val index = gamePlayers.indexOf(gamePlayers.find(p => p.clientId == player.clientId).get)
    gamePlayers = gamePlayers.updated(index, player)

    myCharacter match {
      case p: AlivePlayer =>
        if (p.canCallEmergency(p, emergencyButtons)) {
          controllerRef.get ! ButtonOnController(EmergencyButton())
        } else {
          controllerRef.get ! ButtonOffController(EmergencyButton())
        }
        if (p.canReport(myCharacter.position, deadBodys)) {
          controllerRef.get ! ButtonOnController(ReportButton())
        } else {
          controllerRef.get ! ButtonOffController(ReportButton())
        }
        p match {
          case i: ImpostorAlive =>
            i.canVent(ventList) match {
              case Some(_) => controllerRef.get ! ButtonOnController(VentButton())
              case None => controllerRef.get ! ButtonOffController(VentButton())
            }
            if (i.canKill(myCharacter.position, gamePlayers) && !isTimerOn) {
              controllerRef.get ! ButtonOnController(KillButton())
            } else if (!isTimerOn) {
              controllerRef.get ! ButtonOffController(KillButton())
            }
          case _ =>
        }
      case _ => controllerRef.get ! ButtonOffController(ReportButton())
    }
    gamePlayers
  }

  override def myCharacter: Player = gamePlayers.find(player => player.clientId == this.clientId).get

  override def callEmergency(): Unit = {
    myCharacter match {
      case alive: AlivePlayer => updatePlayer(alive.callEmergency(alive))
      case _ =>
    }
  }

  override def kill(): Unit = {
    myCharacter match {
      case i: ImpostorAlive =>
        i.kill(i.position, gamePlayers) match {
          case Some(player) =>
            val dead = CrewmateGhost(player.color, player.clientId, player.username,
              player.asInstanceOf[CrewmateAlive].numCoins, player.position)
            deadBodys = deadBodys :+ DeadBody(player.color, dead.position)
            updatePlayer(dead)
            controllerRef.get ! UpdatedMyCharController(dead, gamePlayers, deadBodys)
            controllerRef.get ! UpdatedPlayersController(myCharacter, gamePlayers, gameCollectionables, deadBodys)
          case None =>
        }
      case _ =>
    }
  }

  override def killAfterVote(username: String): Unit = {
    val player = gamePlayers.find(p => p.username == username).get
    gamePlayers = gamePlayers.updated(gamePlayers.indexOf(player), player match {
      case i: ImpostorAlive => ImpostorGhost(i.color, i.clientId, i.username, i.position)
      case c: CrewmateAlive => CrewmateGhost(c.color, c.clientId, c.username, c.numCoins, c.position)
      case _ => player
    })
  }

  override def checkTimer(status: TimerStatus): Unit = myCharacter match {
    case _: ImpostorAlive => controllerRef.get ! KillTimerController(status)
    case _ =>
  }

  /**
   * Method of the impostor that allows him to reduce crewmate field of view
   */
  override def sabotage(): Unit = gamePlayers.foreach {
    case aliveCrewmate: CrewmateAlive =>
      val newPlayer = CrewmateAlive(aliveCrewmate.color,
        aliveCrewmate.emergencyCalled, Constants.Crewmate.FIELD_OF_VIEW_SABOTAGE,
        aliveCrewmate.clientId, aliveCrewmate.username, aliveCrewmate.numCoins, aliveCrewmate.position)
      controllerRef.get ! UpdatedMyCharController(newPlayer, gamePlayers, deadBodys)
    case _ =>
  }

  override def sabotageOff(): Unit = gamePlayers.foreach {
    case aliveCrewmate: CrewmateAlive =>
      val newPlayer = CrewmateAlive(aliveCrewmate.color,
        aliveCrewmate.emergencyCalled, Constants.Crewmate.FIELD_OF_VIEW,
        aliveCrewmate.clientId, aliveCrewmate.username, aliveCrewmate.numCoins, aliveCrewmate.position)
      controllerRef.get ! UpdatedMyCharController(newPlayer, gamePlayers, deadBodys)
    case _ =>
  }

  private def generateVentLinks(): Seq[(Vent, Vent)] = {
    var vents: Seq[Vent] = Seq()
    gameMap match {
      case Some(map) => map.foreach(t => {
        t.foreach {
          case v: Vent => vents = vents :+ v
          case _ =>
        }
      })
      case None =>
    }
    var v: Seq[(Vent, Vent)] = Seq()
    for (i <- 0 until vents.length / 2) {
      v = v :+ (vents(i), vents(vents.length - i - 1))
    }
    v
  }

  private def generateEmergencyButtons(): Seq[Emergency] = {
    var emergencyButtons: Seq[Emergency] = Seq()
    gameMap match {
      case Some(map) => map.foreach(t => {
        t.foreach {
          case e: Emergency => emergencyButtons = emergencyButtons :+ e
          case _ =>
        }
      })
      case None =>
    }
    emergencyButtons
  }

  override def removePlayer(clientId: String): Unit = {
    gamePlayers = gamePlayers.filter(player => player.clientId != clientId)
    controllerRef.get ! UpdatedPlayersController(myCharacter, gamePlayers, gameCollectionables, deadBodys)
  }
}