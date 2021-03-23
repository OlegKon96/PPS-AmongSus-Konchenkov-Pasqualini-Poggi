package it.amongsus.core.prolog

import scala.language.implicitConversions
import alice.tuprolog.{Double, Int, Struct, Term}
import it.amongsus.core.map.{Boundary, Emergency, Floor, Other, Tile, Vent, Wall}
import it.amongsus.core.util.{Direction, Point2D}
import scala.collection.convert.ImplicitConversions.`iterator asScala`

object PrologDemonstration {
  /**
   * Implicit to convert a [[Movement]] into a Prolog [[Term]].
   *
   * @param direction the [[Movement]] to convert.
   * @return the Prolog [[Term]] equivalent at the [[Movement]].
   */
  implicit def toPrologDirection(direction : Direction) : Term = {
    Term.createTerm(s"'${direction.toString}'")
  }

  /**
   * Implicit to convert a [[scala.Int]] into a Prolog [[Int]].
   *
   * @param value the [[scala.Int]] to convert.
   * @return the Prolog [[Int]] equivalent at the [[scala.Int]].
   */
  implicit def toPrologInt(value: scala.Int): Int = new Int(value)

  /** Implicit to convert a [[scala.Double]] into a Prolog [[Double]].
   *
   * @param value the [[scala.Double]] to convert.
   * @return the Prolog [[Double]] equivalent at the [[scala.Double]].
   */
  implicit def toPrologDouble(value: scala.Double): Double = new Double(value)

  /**
   * Implicit to convert a [[Point2D]] to a [[Term]].
   *
   * @param point the [[Point2D]] to convert.
   * @return the [[Term]] equivalent at the [[Point2D]].
   */
  implicit def toStructPoint(point: Point2D): Term = new Struct("point", new Int(point.x), new Int(point.y))

  /**
   * Implicit to convert a [[(String, String)]] to a [[Term]].
   *
   * @param player the [[(String, String)]] to convert.
   * @return the [[Term]] equivalent at the [[(String, String)]].
   */
  implicit def toStructPlayer(player: (String, String)): Term = new Struct("player", Term.createTerm(player._1),
    Term.createTerm(player._2))

  /**
   * Implicit to convert a [[Tile]] to a [[Term]].
   *
   * @param tile the [[Tile]] to convert.
   * @return the [[Term]] equivalent at the [[Tile]].
   */
  implicit def toStructTile(tile: Tile): Term = {
    val term = tile match {
      case _ : Emergency => Term.createTerm("'emergency'")
      case _ : Boundary => Term.createTerm("'boundary'")
      case _ : Wall => Term.createTerm("'wall'")
      case _ : Vent =>Term.createTerm("'vent'")
      case _ : Floor => Term.createTerm("'floor'")
      case _ : Other => Term.createTerm("'other'")
    }
    new Struct("tile", term, tile.position)
  }

  /**
   * Implicit to convert a [[List[Int]]] to a [[Term]].
   *
   * @param list the [[List[Int]]] to convert.
   * @return the [[Term]] equivalent at the [[List[Int]]].
   */
  implicit def toStructList(list: List[scala.Int]): Term = {
    val struct = new Struct()
    for { term <- list } struct.append(term)
    struct
  }

  /**
   * Implicit to convert a [[List[(String, String)]]] to a [[Term]].
   *
   * @param list the [[List[(String, String)]]] to convert.
   * @return the [[Term]] equivalent at the [[List[(String, String)]]].
   */
  implicit def toStructPlayer(list: List[(String, String)]): Term = {
    val struct = new Struct()
    for { term <- list } struct.append(term)
    struct
  }

  /**
   * Implicit to convert a [[List[Tile]]] to a [[Term]].
   *
   * @param list the [[List[Tile]]] to convert.
   * @return the [[Term]] equivalent at the [[List[Tile]]].
   */
  implicit def toStructMap(list: List[Tile]): Term = {
    val struct = new Struct()
    for { tile <- list } struct.append(tile)
    struct
  }

  /**
   * Implicit to convert a [[List[Point2D]]] to a [[Term]].
   *
   * @param list the [[List[Point2D]]] to convert.
   * @return the [[Term]] equivalent at the [[List[Point2D]]].
   */
  implicit def toStructPointList(list: List[Point2D]): Term = {
    val struct = new Struct()
    for { term <- list } struct.append(term)
    struct
  }

  /**
   * Implicit to convert a [[Term]] into a [[scala.Int]].
   *
   * @param t the term to convert.
   * @return the equivalent [[scala.Int]] value of the [[Term]].
   */
  implicit def termToInt(t: Term): scala.Int = t.toString.toInt

  /**
   * Implicit to convert a [[Term]] into a [[scala.Double]].
   *
   * @param t the term to convert.
   * @return the equivalent [[scala.Double]] value of the [[Term]].
   */
  implicit def termToDouble(t: Term): scala.Double = t.toString.toDouble

  /**
   * Implicit to convert a [[Term]] to a [[Point2D]].
   *
   * @param t the [[Term]] to convert.
   * @return the [[Point2D]] equivalent at the [[Term]].
   */
  implicit def termToPoint2D(t: Term): Point2D = Point2D(extractVarValue(t, 0), extractVarValue(t, 1))

  /**
   * Implicit to convert a [[Term]] to a [[List[Int]]].
   *
   * @param t the [[Term]] to convert.
   * @return the [[List[Int]]] equivalent at the [[Term]].
   */
  implicit def termToList(t : Term) : List[scala.Int] = {
    var list : List[scala.Int] = List()
    val iterator = t.asInstanceOf[Struct].listIterator()
    iterator.foreach(number => list = list:+ termToInt(number))
    list
  }

  /**
   * Implicit to convert a [[Term]] to a [[List[Point2D]]].
   *
   * @param t the [[Term]] to convert.
   * @return the [[List[Point2D]]] equivalent at the [[Term]].
   */
  implicit def termToPointList(t : Term) : List[Point2D] = {
    var list : List[Point2D] = List()
    val iterator = t.asInstanceOf[Struct].listIterator()
    iterator.foreach(point => list = list:+ termToPoint2D(point))
    list
  }

  /**
   * Gets the i-th [[Term]] contained in the [[Term]] passed as a parameter.
   *
   * @param term the term whence extract the information.
   * @param argNumber the index of the argument to extract.
   * @return the i-th [[Term]] extracted.
   */
  private def extractVarValue(term: Term, argNumber: scala.Int): Term = term.asInstanceOf[Struct].getArg(argNumber)
}