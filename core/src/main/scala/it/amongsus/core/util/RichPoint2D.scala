package it.amongsus.core.util

import it.amongsus.core.util.Direction.{Down, Left, Right, Up}

/**
 * Rich version of Point2D.
 * @param point to which add some functionalities.
 */
case class RichPoint2D(point: Point2D){
  final val MOVEMENT_DISTANCE: Int = 1

  def movePoint(direction: Direction): Point2D = direction match {
    case Up => Point2D(point.x - MOVEMENT_DISTANCE, point.y)
    case Down => Point2D(point.x + MOVEMENT_DISTANCE, point.y)
    case Left => Point2D(point.x, point.y - MOVEMENT_DISTANCE)
    case Right => Point2D(point.x, point.y + MOVEMENT_DISTANCE)
  }
}

/**
 * implicit conversion of Point2D to RichPoint2D.
 */
object RichPoint2D {
  implicit def toMyRichPoint2D(point: Point2D): RichPoint2D = RichPoint2D(point)
}