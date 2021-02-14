package it.amongsus.model.entities.map

trait Collectionable /*extends Entity*/{
  def collect(): Unit
}

object Collectionable{
  def apply(/*position: Point2D*/): Collectionable = CollectionableImpl(/*position*/)
}

case class CollectionableImpl(/*override val position: Point2D*/) extends Collectionable {
  override def collect(): Unit = ???
}