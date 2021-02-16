package it.amongsus.core.entities.player

import it.amongsus.core.entities.map.Collectionable
import it.amongsus.core.entities.util.Point2D

trait CrewmateGhost extends DeadPlayer

object CrewmateGhost{
  def apply(position: Point2D): CrewmateGhost = CrewmateGhostImpl(position)
}

case class CrewmateGhostImpl[A](override val position: Point2D) extends CrewmateGhost {
  override def move(direction: Point2D): Unit = ???

  private def collect(): Unit = Collectionable.apply(position)
}
