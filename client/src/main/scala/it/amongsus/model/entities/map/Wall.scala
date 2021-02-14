package it.amongsus.model.entities.map

trait Wall extends Tile {}

object Wall{
  def apply(/*position: Point2D*/): Wall = WallImpl(/*position*/)
}

case class WallImpl(/*override val position: Point2D*/) extends Wall {
}