package core

import it.amongsus.core.Drawable
import it.amongsus.core.map.MapHelper.{GameMap, generateCoins, generateMap}
import it.amongsus.core.map.Tile
import it.amongsus.core.player.PlayerHelper.checkPosition
import it.amongsus.core.player.{CrewmateAlive, CrewmateGhost, Player}
import it.amongsus.core.util.Point2D
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class CollectCoinTest extends AnyWordSpecLike with BeforeAndAfterAll {

  private final val positionDefault35 = 35
  private val crewmateAlive: Player = CrewmateAlive("green", emergencyCalled = true, "asdasdasd",
    "imCrewmate", 3, Point2D(positionDefault35, positionDefault35))
  private val crewmateGhost: Player = CrewmateGhost("green", "zxcvb", "imCrewmateGhost", 3,
    Point2D(positionDefault35, positionDefault35))
  private val map: GameMap = generateMap(loadMap())
  private val gameCoins = generateCoins(map)

  "A Crewmate Alive" should {
    "Can or Not Collect Coin" in {
      this.crewmateAlive match {
        case crewmate : CrewmateAlive => crewmate.canCollect(gameCoins, checkPosition) match {
          case Some(_) => crewmate.collect()
            assert(crewmate.numCoins == 4)
          case None => assert(crewmate.numCoins == 3)
        }
      }
    }
  }

  "A Crewmate Ghost" should {
    "Can or Not Collect Coin" in {
      this.crewmateGhost match {
        case crewmate : CrewmateGhost => crewmate.canCollect(gameCoins, checkPosition) match {
          case Some(_) => crewmate.collect()
            assert(crewmate.numCoins == 4)
          case None => assert(crewmate.numCoins == 3)
        }
      }
    }
  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/map/gameMap.csv"))
    bufferedSource.getLines
  }
}