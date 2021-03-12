package core

import it.amongsus
import it.amongsus.core.Drawable
import it.amongsus.core.map.{Tile, Vent}
import it.amongsus.core.player.{Crewmate, CrewmateAlive, CrewmateGhost, ImpostorAlive, ImpostorGhost, Player}
import it.amongsus.core.util.Direction.{Down, Up}
import it.amongsus.core.util.Point2D
import it.amongsus.model.actor.ModelActorInfo
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class CoreBaseTest extends AnyWordSpecLike with BeforeAndAfterAll {

  private final val positionDefault34 = 34
  private final val positionDefault35 = 35

  private var crewmateAlive: Player = CrewmateAlive("green", emergencyCalled = true, "asdasdasd",
    "imCrewmate", 3, Point2D(positionDefault35, positionDefault35))
  private var crewmateGhost: Player = CrewmateGhost("green", "zxcvb", "imCrewmateGhost", 3,
    Point2D(positionDefault35, positionDefault35))
  private var impostorAlive: Player = ImpostorAlive("green", emergencyCalled = true,
    "qwerty", "imImpostor", Point2D(positionDefault35, positionDefault35))
  private var impostorGhost: Player = ImpostorGhost("green", "lol", "imImpostorGhost",
    Point2D(positionDefault35, positionDefault35))
  private val modelActor: ModelActorInfo = ModelActorInfo()
  private val map: Array[Array[Drawable[Tile]]] = modelActor.generateMap(loadMap())
  modelActor.generateCollectionables(map)

  "A Crewmate Alive" should {
    "Move" in {
      this.crewmateAlive = crewmateAlive.move(Up(), map).get
      assert(crewmateAlive.position == Point2D(positionDefault34, positionDefault35))
      this.crewmateAlive = crewmateAlive.move(Down(), map).get
      assert(crewmateAlive.position == Point2D(positionDefault35, positionDefault35))
    }

    "Can or Not Collect Coin" in {
      this.crewmateAlive match {
        case crewmate :Crewmate => crewmate.canCollect(modelActor.gameCollectionables, crewmate) match {
          case Some(_) => crewmate.collect(crewmate)
            assert(crewmate.numCoins == 4)
          case None => assert(crewmate.numCoins == 3)
        }
      }
    }
  }

  "An Impostor Alive" should {
    "Move" in {
      this.impostorAlive = impostorAlive.move(Up(), map).get
      assert(impostorAlive.position == Point2D(positionDefault34, positionDefault35))
      this.impostorAlive = impostorAlive.move(Down(), map).get
      assert(impostorAlive.position == Point2D(positionDefault35, positionDefault35))
    }

    "Can Kill Crewmate" in {
      this.impostorAlive match {
        case impostor: ImpostorAlive => assert(impostor.canKill(Point2D(positionDefault35,positionDefault35),
          Seq(crewmateAlive)))
      }
    }

    "Can Use Vent" in {
      this.impostorAlive match {
        case impostor: ImpostorAlive => impostorAlive = impostor.useVent(Seq((Vent(Point2D(positionDefault35,
          positionDefault35)),amongsus.core.map.Vent(Point2D(1,1))))).get
          assert(impostorAlive.position == Point2D(1,1))
      }
    }
  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/images/gameMap.csv"))
    bufferedSource.getLines
  }
}