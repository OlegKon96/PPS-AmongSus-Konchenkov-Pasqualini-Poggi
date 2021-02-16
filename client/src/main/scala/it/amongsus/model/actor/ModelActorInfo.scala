package it.amongsus.model.actor

import akka.actor.ActorRef
import it.amongsus.core.entities.map.{Collectionable, Floor, Tile, Wall}
import it.amongsus.core.entities.player._
import it.amongsus.core.entities.util.Point2D

import scala.Array.ofDim
import scala.util.Random

trait ModelActorInfo {
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

  def gameMap: Option[Array[Array[Tile]]]

  def generateMap(map: Iterator[String]): Array[Array[Tile]]

  def generatePlayers(players: Seq[Player]): Unit

  def playersList: Seq[Player]

  def myCharacter: Player

  def collectionables: Seq[Collectionable]
}

object ModelActorInfo {
  def apply(): ModelActorInfo = ModelActorInfoData(None, None, "")

  def apply(controllerRef: Option[ActorRef], map: Option[Array[Array[Tile]]], clientId: String): ModelActorInfo =
    ModelActorInfoData(controllerRef, map, clientId)
}

case class ModelActorInfoData(override val controllerRef: Option[ActorRef],
                              override val gameMap: Option[Array[Array[Tile]]],
                              override val clientId: String) extends ModelActorInfo {

  val mapCenter: Point2D = Point2D(0, 0)
  var gamePlayers: Seq[Player] = Seq()
  var myChar: Player = Crewmate("", "", mapCenter)
  var collectionablesSeq: Seq[Collectionable] = Seq()

  override def generatePlayers(players: Seq[Player]): Unit = {
    gamePlayers = players
    myChar = gamePlayers.filter(player => player.clientId == this.clientId).lift(0).get
    println("myChar: " + myCharacter.username)
  }

  override def myCharacter: Player = myChar

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
        println(tileMatrix(j)(k))
        k = k + 1
      })
      j = j + 1
      k = 0
    })
    generateCollectionables(tileMatrix)
    tileMatrix
  }

  private def generateCollectionables(map: Array[Array[Tile]]): Unit = {
    var tiles: Seq[Tile] = Seq.empty
    map.foreach(x => x.foreach(y => y match {
      case tile: Floor => tiles = tiles :+ tile
      case tile: Wall =>
    }))

    for (i <- 0 until 10) {
      val rand = Random.nextInt(tiles.length)
      this.collectionablesSeq = collectionables :+ Collectionable(tiles(rand).position)
      tiles = tiles.take(rand) ++ tiles.drop(rand + 1)
    }
    collectionables.foreach(x => println(x.position))
  }

  override def collectionables: Seq[Collectionable] = collectionablesSeq

  override def playersList: Seq[Player] = gamePlayers
}