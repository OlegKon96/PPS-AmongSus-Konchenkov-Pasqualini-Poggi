package prolog

import alice.tuprolog.{Struct, Var}
import it.amongsus.core.map.{Boundary, Emergency, Floor, Other, Tile, Wall}
import it.amongsus.core.player.CrewmateAlive
import it.amongsus.core.util.Direction.{Down, Left, Right, Up}
import it.amongsus.core.util.Point2D
import it.amongsus.core.prolog.PrologEngine.engine
import it.amongsus.core.prolog.PrologDemonstration._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * Object Class that Tests the functions of the Core system
 */
class PrologTest extends AnyWordSpecLike with BeforeAndAfterAll {

  private final val res = new Var("L")
  private final val map : List[Tile] =
    List(Wall(Point2D(1,1)), Floor(Point2D(0,1)), Floor(Point2D(2,2)), Boundary(Point2D(3,3)),
      Emergency(Point2D(5,5)), Other(Point2D(4,4)))
  private final val listOfCoin = List(Point2D(1,1), Point2D(2,1), Point2D(3,7))
  private final val listOfVents = List(Point2D(1,1), Point2D(2,1), Point2D(3,7))

  "A Player" should {
    "Move" in {
      val goal = new Struct("move", Right, Point2D(1,1), res)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      val result : Point2D = solveInfo.getVarValue(res.getName)
      assert(result == Point2D(2,1))
    }
  }

  "A Player Alive" should {
    "Collide with Walls" in {
      val goal = new Struct("moveAlive", Point2D(0,1), map, Right, res)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      val result : Point2D = solveInfo.getVarValue(res.getName)
      assert(result == Point2D(0,1))
    }
  }

  "A Player Alive" should {
    "Move on Floor" in {
      val goal = new Struct("moveAlive", Point2D(1,1), map, Down, res)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      val result : Point2D = solveInfo.getVarValue(res.getName)
      assert(result == Point2D(1,0))
    }
  }

  "A Player Alive" should {
    "Collide with Emergency" in {
      val goal = new Struct("moveAlive", Point2D(5,6), map, Down, res)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      val result : Point2D = solveInfo.getVarValue(res.getName)
      assert(result == Point2D(5,6))
    }
  }

  "A Dead Player" should {
    "Move across Walls" in {
      val goal = new Struct("moveGhost", Point2D(0,1), map, Right, res)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      val result : Point2D = solveInfo.getVarValue(res.getName)
      assert(result == Point2D(1,1))
    }
  }

  "A Dead Player" should {
    "Move on Floor" in {
      val goal = new Struct("moveGhost", Point2D(1,1), map, Down, res)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      val result : Point2D = solveInfo.getVarValue(res.getName)
      assert(result == Point2D(1,0))
    }
  }

  "A Dead Player" should {
    "Collide with Boundary" in {
      val goal = new Struct("moveGhost", Point2D(3,4), List(Boundary(Point2D(3,3))), Down, res)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      val result : Point2D = solveInfo.getVarValue(res.getName)
      assert(result == Point2D(3,4))
    }
  }

  "An Impostor Alive" should {
    "Can Use Vent" in {
      val goal = new Struct("canVent", Point2D(1,1), listOfVents)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      assert(solveInfo.toString == "yes.")
    }
  }

  "An Impostor Alive" should {
    "Cannot Use Vent" in {
      val goal = new Struct("canVent", Point2D(2,2), listOfVents)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      assert(solveInfo.toString == "no.")
    }
  }

  "Check distance" should {
    "Return the distance between two points" in {
      val goal = new Struct("distance", Point2D(1,1), Point2D(2,2), res)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      val result : Double = solveInfo.getVarValue(res.getName)
      assert(result.toInt == 1)
    }
  }

  "Check distance" should {
    "Return all points that have distance below from all points of second list" in {
      val goal = new Struct("checkDistancePointList", Point2D(1,1), List(Point2D(2,2),Point2D(1,2)), 5, res)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      val result : List[Point2D] = solveInfo.getVarValue(res.getName)
      assert(result == List(Point2D(2,2), Point2D(1,2)))
    }
  }

  "The Closest Point" should {
    "Return the closest point" in {
      val goal = new Struct("closestPoint", Point2D(1,1), List(Point2D(4,4),Point2D(2,2)), res)
      val solution = engine(goal)
      val solveInfo = solution.iterator.next()
      val result : Point2D = solveInfo.getVarValue(res.getName)
      assert(result == Point2D(2,2))
    }
  }
}