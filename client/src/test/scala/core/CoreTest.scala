package core

import it.amongsus.core.entities.map.{Tile, Vent}
import it.amongsus.core.entities.player.{Crewmate, CrewmateAlive, ImpostorAlive, Player}
import it.amongsus.core.entities.util.Movement.{Down, Up}
import it.amongsus.core.entities.util.Point2D
import it.amongsus.model.actor.ModelActorInfo
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class CoreTest extends AnyWordSpecLike with BeforeAndAfterAll {

  var crewmateAlive: Player = CrewmateAlive("green", emergencyCalled = true,
    "asdasdasd", "imCrewmate", 3, Point2D(35,35))
  var impostorAlive: Player = ImpostorAlive("green", emergencyCalled = true,
    "qwerty", "imImpostor", Point2D(35,35))
  val modelActor: ModelActorInfo = ModelActorInfo()
  val map: Array[Array[Tile]] = modelActor.generateMap(loadMap())
  modelActor.generateCollectionables(map)

  "A Crewmate Alive" should {
    "Move" in {
      this.crewmateAlive = crewmateAlive.move(Up(), map).get
      assert(crewmateAlive.position == Point2D(34,35))
      this.crewmateAlive = crewmateAlive.move(Down(), map).get
      assert(crewmateAlive.position == Point2D(35,35))
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
      assert(impostorAlive.position == Point2D(34,35))
      this.impostorAlive = impostorAlive.move(Down(), map).get
      assert(impostorAlive.position == Point2D(35,35))
    }

    "Can Kill Crewmate" in {
      this.impostorAlive match {
        case impostor: ImpostorAlive => assert(impostor.canKill(Point2D(35,35), Seq(crewmateAlive)))
      }
    }

    "Can Use Vent" in {
      this.impostorAlive match {
        case impostor: ImpostorAlive => impostorAlive = impostor.useVent(Seq((Vent(Point2D(35,35)),Vent(Point2D(1,1))))).get
          assert(impostorAlive.position == Point2D(1,1))
      }
    }
  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/images/gameMap.csv"))
    bufferedSource.getLines
  }
}