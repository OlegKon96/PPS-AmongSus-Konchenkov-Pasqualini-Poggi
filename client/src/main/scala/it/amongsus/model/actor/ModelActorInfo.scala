package it.amongsus.model.actor

import akka.actor.ActorRef
import it.amongsus.controller.TimerStatus
import it.amongsus.controller.actor.ControllerActorMessages._
import it.amongsus.core
import it.amongsus.core.{Drawable, map}
import it.amongsus.core.util.ButtonType.{EmergencyButton, KillButton, ReportButton, VentButton}
import it.amongsus.core.map.{Boundary, Collectionable, DeadBody, Emergency, Floor, Other, Tile, Vent, Wall}
import it.amongsus.core.player.{AlivePlayer, Crewmate, CrewmateAlive, CrewmateGhost, Impostor, ImpostorAlive, ImpostorGhost, Player}
import it.amongsus.core.util.{Movement, Point2D}

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
  var deadBodies: Seq[DeadBody]
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
  def gameMap: Option[Array[Array[Drawable[Tile]]]]
  /**
   * Method that generates the map of the game
   *
   * @param map of the game
   * @return
   */
  def generateMap(map: Iterator[String]): Array[Array[Drawable[Tile]]]
  /**
   * Method that generates the collectionables of the game
   *
   * @param map of the game
   */
  def generateCollectionables(map: Array[Array[Drawable[Tile]]]): Unit
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
  def sabotage(state: Boolean): Unit
}

object ModelActorInfo {
  def apply(): ModelActorInfo = ModelActorInfoData(None, None, Seq(), Seq(), "", isTimerOn = false)

  def apply(controllerRef: Option[ActorRef], map: Option[Array[Array[Drawable[Tile]]]],
            playersList: Seq[Player], gameCollectionables: Seq[Collectionable], clientId: String): ModelActorInfo =
    ModelActorInfoData(controllerRef, map, playersList, gameCollectionables, clientId, isTimerOn = false)
}

case class ModelActorInfoData(override val controllerRef: Option[ActorRef],
                              override val gameMap: Option[Array[Array[Drawable[Tile]]]],
                              override var gamePlayers: Seq[Player],
                              override var gameCollectionables: Seq[Collectionable],
                              override val clientId: String,
                              override var isTimerOn: Boolean) extends ModelActorInfo {

  private val ventList: Seq[(Drawable[Tile], Drawable[Tile])] = generateVentLinks()
  private val emergencyButtons: Seq[Drawable[Tile]] = generateEmergencyButtons()
  override var deadBodies: Seq[DeadBody] = Seq()

  override def generateMap(map: Iterator[String]): Array[Array[Drawable[Tile]]] = {
    var j = 0
    var k = 0
    val n1 = 50
    val n2 = 72
    val tileMatrix = ofDim[Drawable[Tile]](n1, n2)
    
    map.foreach(line => {
      line.split(",").map(_.trim).foreach(col => {
        col match {
          case "189" => tileMatrix(j)(k) = Wall(Point2D(j, k))
          case "0" => tileMatrix(j)(k) = Other(Point2D(j, k))
          case "40" => tileMatrix(j)(k) = Floor(Point2D(j, k))
          case "1" => tileMatrix(j)(k) = Boundary(Point2D(j, k))
          case "66" => tileMatrix(j)(k) = Vent(Point2D(j, k))
          case "222" => tileMatrix(j)(k) = core.map.Emergency(Point2D(j, k))
        }
        k = k + 1
      })
      j = j + 1
      k = 0
    })
    generateCollectionables(tileMatrix)
    tileMatrix
  }

  override def generateCollectionables(gameMap: Array[Array[Drawable[Tile]]]): Unit = {
    var tiles: Seq[Drawable[Tile]] = for{
      map <- gameMap
      tile <- map.filter{case _: Floor => true case _=> false}
    } yield tile

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
          case crewmate: Crewmate =>
            crewmate.canCollect(gameCollectionables, crewmate) match {
              case Some(collect) =>
                gameCollectionables = gameCollectionables.filter(c => c != collect)
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

  private def playerUpdated(player: Player): Unit = {
    updatePlayer(player)
    controllerRef.get ! UpdatedMyCharController(myCharacter, gamePlayers, deadBodies)
    controllerRef.get ! UpdatedPlayersController(myCharacter, gamePlayers, gameCollectionables, deadBodies)
  }

  override def updatePlayer(player: Player): Seq[Player] = {
    val index = gamePlayers.indexOf(gamePlayers.find(p => p.clientId == player.clientId).get)
    gamePlayers = gamePlayers.updated(index, player)

    myCharacter match {
      case alivePlayer: AlivePlayer =>
        if (alivePlayer.canCallEmergency(alivePlayer, emergencyButtons)) {
          controllerRef.get ! ButtonOnController(EmergencyButton())
        } else {
          controllerRef.get ! ButtonOffController(EmergencyButton())
        }
        if (alivePlayer.canReport(myCharacter.position, deadBodies)) {
          controllerRef.get ! ButtonOnController(ReportButton())
        } else {
          controllerRef.get ! ButtonOffController(ReportButton())
        }
        alivePlayer match {
          case impostorAlive: ImpostorAlive =>
            impostorAlive.canVent(ventList) match {
              case Some(_) => controllerRef.get ! ButtonOnController(VentButton())
              case None => controllerRef.get ! ButtonOffController(VentButton())
            }
            if (impostorAlive.canKill(myCharacter.position, gamePlayers) && !isTimerOn) {
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
      case impostorAlive: ImpostorAlive =>
        impostorAlive.kill(impostorAlive.position, gamePlayers) match {
          case Some(player) =>
            val dead = CrewmateGhost(player.color, player.clientId, player.username,
              player.asInstanceOf[CrewmateAlive].numCoins, player.position)
            deadBodies = deadBodies :+ map.DeadBody(player.color, dead.position)
            updatePlayer(dead)
            controllerRef.get ! UpdatedMyCharController(dead, gamePlayers, deadBodies)
            controllerRef.get ! UpdatedPlayersController(myCharacter, gamePlayers, gameCollectionables, deadBodies)
          case None =>
        }
      case _ =>
    }
  }

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

  /**
   * Method of the impostor that allows him to reduce crewmate field of view
   */
  override def sabotage(state: Boolean): Unit = {
    val newPlayers = myCharacter match {
      case impostor : Impostor => impostor.sabotage(gamePlayers, state)
    }
    newPlayers.foreach(player => controllerRef.get ! UpdatedMyCharController(player, gamePlayers, deadBodies))
  }

  private def generateVentLinks(): Seq[(Drawable[Tile], Drawable[Tile])] = {
    val vents: Seq[Drawable[Tile]] = gameMap match {
      case Some(gameMap) => for{
        map <- gameMap
        tile <- map.filter{case  _:Vent => true case _=> false}
      } yield tile
      case None => Seq()
    }

    var ventTuples: Seq[(Drawable[Tile], Drawable[Tile])] = Seq()
    for (i <- 0 until vents.length / 2) {
      ventTuples = ventTuples :+ (vents(i), vents(vents.length - i - 1))
    }
    ventTuples
  }

  private def generateEmergencyButtons(): Seq[Drawable[Tile]] = {
    gameMap match {
      case Some(_) => for {
        map <- gameMap.get
        tile <- map.filter{ case _: Emergency => true case _ => false}
      } yield tile
      case _ => Seq()
    }
  }

  override def removePlayer(clientId: String): Unit = {
    gamePlayers = gamePlayers.filter(player => player.clientId != clientId)
    controllerRef.get ! UpdatedPlayersController(myCharacter, gamePlayers, gameCollectionables, deadBodies)
  }
}