package core

import it.amongsus.core.entities.Drawable
import it.amongsus.core.entities.map.Tile
import it.amongsus.core.entities.player.{CrewmateAlive, CrewmateGhost, Player}
import it.amongsus.core.entities.util.Point2D
import it.amongsus.model.actor.ModelActorInfo
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class CollectCoinTest extends AnyWordSpecLike with BeforeAndAfterAll {

  private final val positionDefault35 = 35

  private val crewmateAlive: Player = CrewmateAlive("green", emergencyCalled = true, "asdasdasd",
    "imCrewmate", 3, Point2D(positionDefault35, positionDefault35))
  private val crewmateGhost: Player = CrewmateGhost("green", "zxcvb", "imCrewmateGhost", 3,
    Point2D(positionDefault35, positionDefault35))
  private val modelActor: ModelActorInfo = ModelActorInfo()
  private val map: Array[Array[Drawable[Tile]]] = modelActor.generateMap(loadMap())
  modelActor.generateCollectionables(map)

  "A Crewmate Alive" should {
    "Can or Not Collect Coin" in {
      this.crewmateAlive match {
        case crewmate : CrewmateAlive => crewmate.canCollect(modelActor.gameCollectionables, crewmate) match {
          case Some(_) => crewmate.collect(crewmate)
            assert(crewmate.numCoins == 4)
          case None => assert(crewmate.numCoins == 3)
        }
      }
    }
  }

  "A Crewmate Ghost" should {
    "Can or Not Collect Coin" in {
      this.crewmateGhost match {
        case crewmate : CrewmateGhost => crewmate.canCollect(modelActor.gameCollectionables, crewmate) match {
          case Some(_) => crewmate.collect(crewmate)
            assert(crewmate.numCoins == 4)
          case None => assert(crewmate.numCoins == 3)
        }
      }
    }
  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/images/gameMap.csv"))
    bufferedSource.getLines
  }
}