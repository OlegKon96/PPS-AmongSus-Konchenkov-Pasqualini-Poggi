package it.amongsus.core.entities.map

import it.amongsus.core.entities.Entity
import it.amongsus.core.entities.util.Point2D

/**
 * Trait that manages the report of a dead body
 */
trait DeadBody extends Entity{
  /**
   * Method to report a dead body found
   */
  def report() : Unit
}

object DeadBody{
  def apply(color: String, position: Point2D): DeadBody = DeadBodyImpl(color, position)

  private case class DeadBodyImpl(override val color: String, override val position: Point2D) extends DeadBody {
    override def report(): Unit = ???
  }
}