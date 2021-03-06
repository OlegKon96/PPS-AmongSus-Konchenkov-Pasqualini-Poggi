package core

import it.amongsus.core.entities.map.{Emergency, Tile}
import it.amongsus.core.entities.player.{CrewmateAlive, ImpostorAlive, Player}
import it.amongsus.core.entities.util.Point2D
import it.amongsus.model.actor.ModelActorInfo
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class CallEmergencyTest extends AnyWordSpecLike with BeforeAndAfterAll {

  private final val positionDefault35 = 35

  private var crewmateAlive: Player = CrewmateAlive("green", emergencyCalled = false, "asdasdasd",
    "imCrewmate", 3, Point2D(positionDefault35, positionDefault35))
  private var impostorAlive: Player = ImpostorAlive("green", emergencyCalled = false,
    "qwerty", "imImpostor", Point2D(positionDefault35, positionDefault35))
  private val modelActor: ModelActorInfo = ModelActorInfo()
  private val map: Array[Array[Tile]] = modelActor.generateMap(loadMap())
  modelActor.generateCollectionables(map)

  "A Crewmate Alive" should {
    "Can Call Emergency" in {
      this.crewmateAlive match {
        case crewmate : CrewmateAlive => assert(crewmate.canCallEmergency(crewmate,
          Seq(Emergency(Point2D(positionDefault35, positionDefault35)))))
          this.crewmateAlive = CrewmateAlive("green", emergencyCalled = true, "asdasdasd", "imCrewmate",
            3, Point2D(positionDefault35, positionDefault35))
      }
    }

    "Can't Call Emergency Twice in a Game" in {
      this.crewmateAlive match {
        case crewmate : CrewmateAlive => assert(crewmate.emergencyCalled)
      }
    }
  }

  "An Impostor Alive" should {
    "Can Call Emergency" in {
      this.impostorAlive match {
        case impostor : ImpostorAlive => assert(impostor.canCallEmergency(impostor,
          Seq(Emergency(Point2D(positionDefault35, positionDefault35)))))
          this.impostorAlive = ImpostorAlive("green", emergencyCalled = true, "qwerty", "imImpostor",
            Point2D(positionDefault35, positionDefault35))
      }
    }

    "Can't Call Emergency Twice in a Game" in {
      this.impostorAlive match {
        case impostor : ImpostorAlive => assert(impostor.emergencyCalled)
      }
    }
  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/images/gameMap.csv"))
    bufferedSource.getLines
  }
}