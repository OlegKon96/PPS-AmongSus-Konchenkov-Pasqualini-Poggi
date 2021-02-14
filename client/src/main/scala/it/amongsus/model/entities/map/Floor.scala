package it.amongsus.model.entities.map

trait Floor extends Tile {}

object Floor{
  def apply(/*position: Point2D*/): Floor = FloorImpl(/*position*/)
}

case class FloorImpl(/*override val position: Point2D*/) extends Floor {
}