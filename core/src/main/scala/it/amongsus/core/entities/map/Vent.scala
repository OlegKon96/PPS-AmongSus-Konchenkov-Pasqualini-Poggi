package it.amongsus.core.entities.map

import it.amongsus.core.entities.util.Point2D

/**
 * Trait that manages the vent of the game
 */
trait Vent extends Floor{
  /**
   * Method to manages the use of the vent
   */
  def useVent(): Unit
}

object Vent{
  def apply(position: Point2D): Vent = VentImpl(position)

  private case class VentImpl(override val position: Point2D) extends Vent {
    override def useVent(): Unit = ???
  }
}