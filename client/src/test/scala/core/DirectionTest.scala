package core

import it.amongsus.core.Drawable
import it.amongsus.core.map.Tile
import it.amongsus.core.player.{CrewmateAlive, CrewmateGhost, ImpostorAlive, ImpostorGhost, Player}
import it.amongsus.core.util.Direction.{Down, Up}
import it.amongsus.core.util.Point2D
import it.amongsus.model.actor.ModelActorInfo
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class DirectionTest extends AnyWordSpecLike with BeforeAndAfterAll {

  private final val positionDefault30 = 30
  private final val positionDefault31 = 31
  private final val positionDefault32 = 32
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
  modelActor.generateCoins(map)

  "A Crewmate Alive" should {
    "Normally Move" in {
      this.crewmateAlive = crewmateAlive.move(Up(), map).get
      assert(crewmateAlive.position == Point2D(positionDefault34, positionDefault35))
      this.crewmateAlive = crewmateAlive.move(Down(), map).get
      assert(crewmateAlive.position == Point2D(positionDefault35, positionDefault35))
    }

    "Move Not Across a Wall" in {
      this.crewmateAlive = crewmateAlive.move(Up(), map).get
      this.crewmateAlive = crewmateAlive.move(Up(), map).get
      this.crewmateAlive = crewmateAlive.move(Up(), map).get
      this.crewmateAlive = crewmateAlive.move(Up(), map).get
      assertThrows[NoSuchElementException] {
        this.crewmateAlive = crewmateAlive.move(Up(), map).get
      }
      assert(crewmateAlive.position == Point2D(positionDefault31, positionDefault35))
      this.crewmateAlive = crewmateAlive.move(Down(), map).get
      assert(crewmateAlive.position == Point2D(positionDefault32, positionDefault35))
    }
  }

  "A Crewmate Ghost" should {
    "Normally Move" in {
      this.crewmateGhost = crewmateGhost.move(Up(), map).get
      assert(crewmateGhost.position == Point2D(positionDefault34, positionDefault35))
      this.crewmateGhost = crewmateGhost.move(Down(), map).get
      assert(crewmateGhost.position == Point2D(positionDefault35, positionDefault35))
    }

    "Move Across Wall" in {
      this.crewmateGhost = crewmateGhost.move(Up(), map).get
      this.crewmateGhost = crewmateGhost.move(Up(), map).get
      this.crewmateGhost = crewmateGhost.move(Up(), map).get
      this.crewmateGhost = crewmateGhost.move(Up(), map).get
      this.crewmateGhost = crewmateGhost.move(Up(), map).get
      assert(crewmateGhost.position == Point2D(positionDefault30, positionDefault35))
      this.crewmateGhost = crewmateGhost.move(Down(), map).get
      assert(crewmateGhost.position == Point2D(positionDefault31, positionDefault35))
    }
  }

  "An Impostor Alive" should {
    "Normally Move" in {
      this.impostorAlive = impostorAlive.move(Up(), map).get
      assert(impostorAlive.position == Point2D(positionDefault34, positionDefault35))
      this.impostorAlive = impostorAlive.move(Down(), map).get
      assert(impostorAlive.position == Point2D(positionDefault35, positionDefault35))
    }

    "Move Not Across a Wall" in {
      this.impostorAlive = impostorAlive.move(Up(), map).get
      this.impostorAlive = impostorAlive.move(Up(), map).get
      this.impostorAlive = impostorAlive.move(Up(), map).get
      this.impostorAlive = impostorAlive.move(Up(), map).get
      assertThrows[NoSuchElementException] {
        this.impostorAlive = impostorAlive.move(Up(), map).get
      }
      assert(impostorAlive.position == Point2D(positionDefault31, positionDefault35))
      this.impostorAlive = impostorAlive.move(Down(), map).get
      assert(impostorAlive.position == Point2D(positionDefault32, positionDefault35))
    }
  }

  "An Impostor Ghost" should {
    "Normally Move" in {
      this.impostorGhost = impostorGhost.move(Up(), map).get
      assert(impostorGhost.position == Point2D(positionDefault34, positionDefault35))
      this.impostorGhost = impostorGhost.move(Down(), map).get
      assert(impostorGhost.position == Point2D(positionDefault35, positionDefault35))
    }

    "Move Across Wall" in {
      this.impostorGhost = impostorGhost.move(Up(), map).get
      this.impostorGhost = impostorGhost.move(Up(), map).get
      this.impostorGhost = impostorGhost.move(Up(), map).get
      this.impostorGhost = impostorGhost.move(Up(), map).get
      this.impostorGhost = impostorGhost.move(Up(), map).get
      assert(impostorGhost.position == Point2D(positionDefault30, positionDefault35))
      this.impostorGhost = impostorGhost.move(Down(), map).get
      assert(impostorGhost.position == Point2D(positionDefault31, positionDefault35))
    }
  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/images/gameMap.csv"))
    bufferedSource.getLines
  }
}