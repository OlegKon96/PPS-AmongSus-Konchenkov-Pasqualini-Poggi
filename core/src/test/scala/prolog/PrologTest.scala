package prolog

import alice.tuprolog.{Struct, Var}
import it.amongsus.core.map.{Floor, Tile, Wall}
import it.amongsus.core.player.CrewmateAlive
import it.amongsus.core.util.Direction.Right
import it.amongsus.core.util.Point2D
import it.amongsus.core.prolog.PrologEngine.engine
import it.amongsus.core.prolog.PrologDemonstration._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * Object Class that Tests the functions of the Core system
 */
class PrologTest extends AnyWordSpecLike with BeforeAndAfterAll {

  //private final val list = List(1,2,3)
  private final val direction = Right
  private final val position = Point2D(1, 1)
  private final val position2 = Point2D(0, 1)
  private final val position3 = Point2D(2, 2)
  private final val length = new Var("L")
  //private final val number : Int = 2
  private final val map : List[Tile] = List(Wall(position), Floor(position2), Floor(position3))
  //private final val listOfPlayer = List(Point2D(1,1), Point2D(1,2))
  private final val listOfCoin = List(Point2D(1,1), Point2D(2,1), Point2D(3,7))

  "A Player" should {
    "Move" in {
      val goal = new Struct("move",direction, position, length)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      val result : Point2D = solveInfo.getVarValue(length.getName)
      //println("move: " + result)
      assert(result == Point2D(2,1))
    }
  }

  "A Player Alive" should {
    "Check Collision with Walls" in {
      val goal3 = new Struct("moveAlive", position2, map, direction, length)
      val solution3 = engine(goal3)
      val solveInfo3 = solution3.iterator.next()
      val result3 : Point2D = solveInfo3.getVarValue(length.getName)
      //println("move alive: " + result3)
      assert(result3 == Point2D(0,1))
    }
  }

  "An Impostor Alive" should {
    "Can Use Vent" in {
      val goalCheckDistance = new Struct("canVent", position, listOfCoin)
      val solution2 = engine(goalCheckDistance)
      val solveInfo2 = solution2.iterator.next()
      //println("canVent: " + solveInfo2)
      assert(solveInfo2.toString == "yes.")
    }
  }
}