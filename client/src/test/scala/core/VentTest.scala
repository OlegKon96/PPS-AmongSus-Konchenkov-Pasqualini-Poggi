package core

import it.amongsus.core.entities.map.{Tile, Vent}
import it.amongsus.core.entities.player.{ImpostorAlive, Player}
import it.amongsus.core.entities.util.Point2D
import it.amongsus.model.actor.ModelActorInfo
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class VentTest extends AnyWordSpecLike with BeforeAndAfterAll {

  private final val positionDefault35 = 35

  private var impostorAlive: Player = ImpostorAlive("green", emergencyCalled = true,
    "qwerty", "imImpostor", Point2D(positionDefault35, positionDefault35))
  private val modelActor: ModelActorInfo = ModelActorInfo()
  private val map: Array[Array[Tile]] = modelActor.generateMap(loadMap())
  modelActor.generateCollectionables(map)

  "An Impostor Alive" should {
    "Can Use Vent" in {
      this.impostorAlive match {
        case impostor: ImpostorAlive => impostorAlive = impostor.useVent(Seq((Vent(Point2D(positionDefault35,
          positionDefault35)), Vent(Point2D(1, 1))))).get
          assert(impostorAlive.position == Point2D(1, 1))
      }
    }
  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/images/gameMap.csv"))
    bufferedSource.getLines
  }
}