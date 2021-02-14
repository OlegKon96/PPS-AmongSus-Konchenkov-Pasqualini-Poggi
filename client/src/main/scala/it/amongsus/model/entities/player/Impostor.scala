package it.amongsus.model.entities.player

import it.amongsus.model.entities.util.Point2D

/**
 * Trait that manages the Impostor of the game
 */
trait Impostor extends AlivePlayer{
  /**
   * Manages the Impostar that killed a Crewmate
   */
  def kill(): Unit
  /**
   * Manages the use of the vent
   */
  def useVent():Unit
  /**
   * Manages the sabotage of the game
   */
  def sabotage():Unit
}

object Impostor{
  def apply(position: Point2D): Impostor = ImpostorImpl(position)
}

case class ImpostorImpl(override val position: Point2D) extends Impostor {
  override def move(direction: Point2D): Unit = ???

  override def vote(): Unit = ???

  override def report(): Unit = ???

  override def callEmergency(): Unit = ???

  override def kill(): Unit = ???

  override def useVent(): Unit = ???

  override def sabotage(): Unit = ???
}
