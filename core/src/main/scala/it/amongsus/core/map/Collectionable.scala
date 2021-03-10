package it.amongsus.core.map

import it.amongsus.core.Entity
import it.amongsus.core.util.Point2D

/**
 * Trait of the collectionable of the game that the player should collect
 */
trait Collectionable extends Entity[Collectionable]{
  /**
   * Take the coin
   */
  def collect(): Unit
}

object Collectionable{
  def apply(position: Point2D): Collectionable = CollectionableImpl("yellow", position)

  private case class CollectionableImpl(override val color: String,
                                        override val position: Point2D) extends Collectionable {
    override def collect(): Unit = ???
  }
}