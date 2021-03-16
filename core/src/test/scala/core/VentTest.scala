package core

import it.amongsus
import it.amongsus.core.Drawable
import it.amongsus.core.map.MapHelper.generateMap
import it.amongsus.core.map.{Tile, Vent}
import it.amongsus.core.player.{ImpostorAlive, Player}
import it.amongsus.core.util.Point2D
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class VentTest extends AnyWordSpecLike with BeforeAndAfterAll {

  private final val positionDefault35 = 35
  private var impostorAlive: Player = ImpostorAlive("green", emergencyCalled = true,
    "qwerty", "imImpostor", Point2D(positionDefault35, positionDefault35))
  private val map: Array[Array[Drawable[Tile]]] = generateMap(loadMap())

  "An Impostor Alive" should {
    "Can Use Vent" in {
      this.impostorAlive match {
        case impostor: ImpostorAlive => impostorAlive = impostor.useVent(Seq((Vent(Point2D(positionDefault35,
          positionDefault35)), amongsus.core.map.Vent(Point2D(1, 1))))).get
          assert(impostorAlive.position == Point2D(1, 1))
      }
    }
  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/map/gameMap.csv"))
    bufferedSource.getLines
  }
}