package it.amongsus.model.actor

import akka.actor.ActorRef
import it.amongsus.controller.TimerStatus
import it.amongsus.controller.actor.ControllerActorMessages.{ButtonOffController, ButtonOnController, KillTimerController, UpdatedMyCharController, UpdatedPlayersController}
import it.amongsus.core.entities.map.{Boundary, Collectionable, DeadBody, Emergency, Floor, Other, Tile, Vent, Wall}
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
   * Method of the Impostor to kill a player
   */
  def kill(): Unit

  def checkTimer(status: TimerStatus): Unit
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
      case tile: Wall =>
      case tile: Other =>
    })

    for (i <- 0 until 10) {
      val rand = Random.nextInt(tiles.length)
      this.gameCollectionables = gameCollectionables :+ Collectionable(tiles(rand).position)
      tiles = tiles.take(rand) ++ tiles.drop(rand + 1)
    }
  }

  override def myCharacter: Player = gamePlayers.find(player => player.clientId == this.clientId).get

  override def updateMyChar(direction: Movement): Unit = {
    myCharacter.move(direction, gameMap.get) match {
      case Some(player) =>
        playerUpdated(player match {
          case crew: Crewmate =>
            crew.canCollect(gameCollectionables, crew) match {
              case Some(collect) => {
                gameCollectionables = gameCollectionables.filter(c => c != collect)
                crew.collect(crew)
              }
              case None => crew
            }
          case _ => player
        })
      case None =>
    }
  }

  override def updatePlayer(player: Player): Seq[Player] = {
    val index = gamePlayers.indexOf(gamePlayers.find(p => p.clientId == player.clientId).get)
    gamePlayers = gamePlayers.updated(index, player)
    myCharacter match {
      case p: AlivePlayer =>
        p match {
          case i: ImpostorAlive =>
            i.canVent(ventList) match {
              case Some(pos) => controllerRef.get ! ButtonOnController(VentButton())
              case None => controllerRef.get ! ButtonOffController(VentButton())
            }

            if (i.canKill(myCharacter.position, gamePlayers) && !isTimerOn) {
              controllerRef.get ! ButtonOnController(KillButton())
            } else if(!isTimerOn ) {
              controllerRef.get ! ButtonOffController(KillButton())
            }
          case _ =>
        }
      case _ => controllerRef.get ! ButtonOffController(ReportButton())
    }
    gamePlayers
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

  private def playerUpdated(player: Player): Unit = {
    updatePlayer(player)
    controllerRef.get ! UpdatedMyCharController(myCharacter, gamePlayers, deadBodys)
    controllerRef.get ! UpdatedPlayersController(myCharacter, gamePlayers, gameCollectionables, deadBodys)
  }

  override def kill(): Unit = {
    myCharacter match {
      case i: ImpostorAlive =>
        i.kill(i.position, gamePlayers) match {
          case Some(player) =>
            val dead = CrewmateGhost(player.clientId, player.username, player.asInstanceOf[CrewmateAlive].numCoins,
              player.position)
            deadBodys = deadBodys :+ DeadBody(dead.position)
            updatePlayer(dead)
            controllerRef.get ! UpdatedMyCharController(dead, gamePlayers, deadBodys)
            controllerRef.get ! UpdatedPlayersController(myCharacter, gamePlayers, gameCollectionables, deadBodys)
          case None =>
        }
      case _ =>
    }
  }

  override def checkTimer(status: TimerStatus): Unit = myCharacter match {
    case i : ImpostorAlive => controllerRef.get ! KillTimerController(status)
    case _ =>
  }
}