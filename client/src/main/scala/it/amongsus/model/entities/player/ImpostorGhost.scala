package it.amongsus.model.entities.player

import it.amongsus.model.entities.util.Point2D

/**
 * Trait that manages the Ghost of the Impostor of the game
 */
trait ImpostorGhost extends DeadPlayer{
  /**
   * Manages the Impostor that can sabotage an item of the game
   */
  def sabotage():Unit
}

object ImpostorGhost{
  def apply(position: Point2D): ImpostorGhost = ImpostorGhostImpl(position)
}

case class ImpostorGhostImpl(override val position: Point2D) extends ImpostorGhost {
  override def move(direction: Point2D): Unit = ???

  override def sabotage(): Unit = ???
}
