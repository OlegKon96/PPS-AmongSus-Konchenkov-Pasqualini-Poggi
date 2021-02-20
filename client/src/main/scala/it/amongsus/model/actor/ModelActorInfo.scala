package it.amongsus.model.actor

import akka.actor.ActorRef
import it.amongsus.controller.actor.ControllerActorMessages.{ButtonOffController, ButtonOnController}
import it.amongsus.core.entities.map.{Collectionable, DeadBody, Emergency, Floor, Other, Tile, Vent, Wall}
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
   * Sequence of a players' DeadBody
   */
  var deadBodys: Seq[DeadBody]
}

object ModelActorInfo {
  def apply(): ModelActorInfo = ModelActorInfoData(None, None, Seq(), Seq(), "")

  def apply(controllerRef: Option[ActorRef], map: Option[Array[Array[Tile]]],
            playersList: Seq[Player], gameCollectionables: Seq[Collectionable], clientId: String): ModelActorInfo =
    ModelActorInfoData(controllerRef, map, playersList, gameCollectionables, clientId)
}

case class ModelActorInfoData(override val controllerRef: Option[ActorRef],
                              override val gameMap: Option[Array[Array[Tile]]],
                              override var gamePlayers: Seq[Player],
                              override var gameCollectionables: Seq[Collectionable],
                              override val clientId: String) extends ModelActorInfo {

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
          case "0" => tileMatrix(j)(k) = Wall(Point2D(j, k))
          case "40" => tileMatrix(j)(k) = Floor(Point2D(j, k))
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
    gamePlayers
  }
}