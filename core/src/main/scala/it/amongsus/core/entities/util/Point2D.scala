package it.amongsus.core.entities.util

trait Point2D {
  def x: Int

  def y: Int

  def distance(point: Point2D): Int
}

object Point2D {
  def apply(x: Int, y: Int): Point2D = Point2DImpl(x, y)

  private case class Point2DImpl(override val x: Int,
                                 override val y: Int) extends Point2D {

    override def distance(point: Point2D): Int =
      scala.math.sqrt((point.x - this.x) * (point.x - this.x) + (point.y - this.y) * (point.y - this.y)).toInt
  }

}

