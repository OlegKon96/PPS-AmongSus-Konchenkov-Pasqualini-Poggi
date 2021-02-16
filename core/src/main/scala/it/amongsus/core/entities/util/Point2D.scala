package it.amongsus.core.entities.util

trait Point2D{
  def x: Int
  def y: Int
}

object Point2D{
  def apply(x: Int, y: Int): Point2D = Point2DImpl(x,y)
}

case class Point2DImpl(override val x: Int,
                   override val y : Int) extends Point2D