package it.amongsus.core.util

import it.amongsus.core.Drawable
import it.amongsus.core.map._
import it.amongsus.core.util.MapHelper.GameMap

import scala.Array.ofDim
import scala.util.Random

trait MapHelper {
  /**
   * Method that generates the map of the game.
   *
   * @param map of the game.
   * @return game map.
   */
  def generateMap(map: Iterator[String]): GameMap

  /**
   * Method that generates the coins of the game.
   *
   * @param map of the game.
   */
  def generateCoins(map: GameMap): Seq[Coin]

  /**
   *
   * @param gameMap
   * @return
   */
  def generateVentLinks(gameMap: GameMap): Seq[(Drawable[Tile], Drawable[Tile])]

  /**
   *
   * @param gameMap
   * @return
   */
  def generateEmergencyButtons(gameMap: GameMap): Seq[Drawable[Tile]]
}

object MapHelper extends MapHelper{
  private final val ROWS: Int = 50
  private final val COLS: Int = 72
  private final val NUM_COINS: Int = 10
  private final val ONE: Int = 1
  private final val TWO: Int = 2

  type GameMap = Array[Array[Drawable[Tile]]]

  override def generateMap(map: Iterator[String]): GameMap = {
    val tileMatrix = ofDim[Drawable[Tile]](ROWS, COLS)
    for {
      (line, j) <- map.zipWithIndex
      (tile, k) <- line.split(",").map(_.trim).zipWithIndex
    } tile match {
      case "189" => tileMatrix(j)(k) = Wall(Point2D(j, k))
      case "0" => tileMatrix(j)(k) = Other(Point2D(j, k))
      case "40" => tileMatrix(j)(k) = Floor(Point2D(j, k))
      case "1" => tileMatrix(j)(k) = Boundary(Point2D(j, k))
      case "66" => tileMatrix(j)(k) = Vent(Point2D(j, k))
      case "222" => tileMatrix(j)(k) = Emergency(Point2D(j, k))
    }
    tileMatrix
  }

  override def generateCoins(gameMap: GameMap): Seq[Coin] = {
    var tiles: Seq[Drawable[Tile]] = for {
      map <- gameMap
      tile <- map.filter { case _: Floor => true case _ => false }
    } yield tile

    var gameCoins: Seq[Coin] = Seq()
    for (_ <- 0 until NUM_COINS) {
      val rand = Random.nextInt(tiles.length)
      gameCoins = gameCoins :+ Coin(tiles(rand).position)
      tiles = tiles.take(rand) ++ tiles.drop(rand + ONE)
    }
    gameCoins
  }

  override def generateVentLinks(gameMap: GameMap): Seq[(Drawable[Tile], Drawable[Tile])] = {
    val vents: Seq[Drawable[Tile]] = for {
        map <- gameMap
        tile <- map.filter { case _: Vent => true case _ => false }
      } yield tile

    var ventTuples: Seq[(Drawable[Tile], Drawable[Tile])] = Seq()
    for (i <- 0 until vents.length / TWO) {
      ventTuples = ventTuples :+ (vents(i), vents(vents.length - i - ONE))
    }
    ventTuples
  }

  override def generateEmergencyButtons(gameMap: GameMap): Seq[Drawable[Tile]] = {
    for {
        map <- gameMap
        tile <- map.filter { case _: Emergency => true case _ => false }
      } yield tile
  }
}
