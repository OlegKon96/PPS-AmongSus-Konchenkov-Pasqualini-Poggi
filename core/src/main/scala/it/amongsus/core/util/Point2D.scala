package it.amongsus.core.util

/**
 * Trait that manages the Point2D of the game
 */
trait Point2D {
  /**
   * X Axis
   *
   * @return x value of the point
   */
  def x: Int
  /**
   * Y Axis
   *
   * @return y value of the point
   */
  def y: Int
  /**
   * Method to find the distance from 2 points
   *
   * @param point point 2D
   * @return the distance between two points
   */
  def distance(point: Point2D): Int
}

object Point2D {
  def apply(x: Int, y: Int): Point2D = Point2DImpl(x, y)

  private case class Point2DImpl(override val x: Int, override val y: Int) extends Point2D {

    override def distance(point: Point2D): Int =
      scala.math.sqrt((point.x - this.x) * (point.x - this.x) + (point.y - this.y) * (point.y - this.y)).toInt
  }
}