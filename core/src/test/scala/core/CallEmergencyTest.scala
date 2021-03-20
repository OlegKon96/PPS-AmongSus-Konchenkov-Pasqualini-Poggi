package core

import it.amongsus
import it.amongsus.core.map.Emergency
import it.amongsus.core.player.PlayerHelper.{checkPosition, emergencyDistance}
import it.amongsus.core.player.{CrewmateAlive, ImpostorAlive, Player}
import it.amongsus.core.util.Point2D
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class CallEmergencyTest extends AnyWordSpecLike with BeforeAndAfterAll {

  private final val positionDefault35 = 35
  private var crewmateAlive: Player = CrewmateAlive("green", emergencyCalled = false, "asdasdasd",
    "imCrewmate", 3, Point2D(positionDefault35, positionDefault35))
  private var impostorAlive: Player = ImpostorAlive("green", emergencyCalled = false,
    "qwerty", "imImpostor", Point2D(positionDefault35, positionDefault35))

  "A Crewmate Alive" should {
    "Can Call Emergency" in {
      this.crewmateAlive match {
        case crewmate : CrewmateAlive => assert(crewmate.canCallEmergency(
          Seq(Emergency(Point2D(positionDefault35, positionDefault35))), emergencyDistance))
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
        case impostor : ImpostorAlive => assert(impostor.canCallEmergency(
          Seq(amongsus.core.map.Emergency(Point2D(positionDefault35, positionDefault35))), emergencyDistance))
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
}