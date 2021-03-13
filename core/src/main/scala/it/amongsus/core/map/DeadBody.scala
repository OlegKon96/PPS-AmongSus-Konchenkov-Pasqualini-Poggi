package it.amongsus.core.map

import it.amongsus.core.Entity
import it.amongsus.core.util.Point2D

/**
 * Trait that represents the report of a dead body
 */
trait DeadBody extends Entity[DeadBody]

object DeadBody{
  def apply(color: String, position: Point2D): DeadBody = DeadBodyImpl(color, position)

  private case class DeadBodyImpl(override val color: String, override val position: Point2D) extends DeadBody
}