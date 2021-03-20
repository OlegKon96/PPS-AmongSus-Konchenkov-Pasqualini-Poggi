package core

import it.amongsus
import it.amongsus.core.map.DeadBody
import it.amongsus.core.player.PlayerHelper.reportDistance
import it.amongsus.core.player.{CrewmateAlive, ImpostorAlive, Player}
import it.amongsus.core.util.Point2D
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class KillTest extends AnyWordSpecLike with BeforeAndAfterAll {

  private final val positionDefault35 = 35
  private val crewmateAlive: Player = CrewmateAlive("green", emergencyCalled = true, "asdasdasd",
    "imCrewmate", 3, Point2D(positionDefault35, positionDefault35))
  private val deadCrewmate: DeadBody = amongsus.core.map.DeadBody("green",
    Point2D(positionDefault35, positionDefault35))
  private val impostorAlive: Player = ImpostorAlive("green", emergencyCalled = true,
    "qwerty", "imImpostor", Point2D(positionDefault35, positionDefault35))

  "An Impostor Alive" should {
    "Can Kill Crewmate" in {
      this.impostorAlive match {
        case impostor: ImpostorAlive => assert(impostor.canKill(Seq(crewmateAlive)))
      }
    }

    "Can Report Kill" in {
      this.impostorAlive match {
        case impostor: ImpostorAlive => assert(impostor.canReport(Seq(deadCrewmate), reportDistance))
      }
    }
  }

  "A Crewmate Alive" should {
    "Can Report Kill" in {
      this.crewmateAlive match {
        case crewmate: CrewmateAlive => assert(crewmate.canReport(Seq(deadCrewmate), reportDistance))
      }
    }
  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/map/gameMap.csv"))
    bufferedSource.getLines
  }
}