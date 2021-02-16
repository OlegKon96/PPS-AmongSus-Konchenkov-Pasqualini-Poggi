package it.amongsus.core.entities.player

import java.awt.Color

import it.amongsus.core.entities.player.Movement.{Down, Left, Right, Up}
import it.amongsus.core.entities.util.Point2D

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
  def apply(clientId: String, username: String, position: Point2D): Impostor =
    ImpostorImpl("green", clientId, username, position)
}

case class ImpostorImpl(override val color: String,
                        override val clientId: String,
                        override val username: String,
                        override val position: Point2D) extends Impostor {

  override def move(direction: Movement): Unit = direction match{
    case Up() => Impostor(clientId, username, Point2D(position.x, position.y + 1))
    case Down() => Impostor(clientId, username, Point2D(position.x, position.y - 1))
    case Left() => Impostor(clientId, username, Point2D(position.x - 1, position.y))
    case Right() => Impostor(clientId,username, Point2D(position.x + 1, position.y))
  }

  override def vote(): Unit = ???

  override def report(): Unit = ???

  override def callEmergency(): Unit = ???

  override def kill(): Unit = ???

  override def useVent(): Unit = ???

  override def sabotage(): Unit = ???
}
