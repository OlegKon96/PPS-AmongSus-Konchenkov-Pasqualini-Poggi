package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO

/**
 *
 */
trait GameFrame {
  /**
   *
   * @return
   */
  def start(): IO[Unit]
}

object GameFrame {
  def apply(guiRef: Option[ActorRef],menuFrame: MenuFrame): GameFrame = new GameFrameImpl(guiRef, menuFrame)

  private class GameFrameImpl(guiRef: Option[ActorRef],menuFrame: MenuFrame) extends GameFrame {
    override def start(): IO[Unit] = ???
  }
}