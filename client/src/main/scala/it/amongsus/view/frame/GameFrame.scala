package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.view.swingio.JFrameIO

import javax.swing.JFrame

/**
 *
 */
trait GameFrame extends Frame {
  /**
   *
   * @return
   */
  def start(): IO[Unit]
}

object GameFrame {
  def apply(guiRef: Option[ActorRef]): GameFrame = new GameFrameImpl(guiRef)

  private class GameFrameImpl(guiRef: Option[ActorRef]) extends GameFrame {
    val gameFrame = new JFrameIO(new JFrame("Among Sus"))
    val WIDTH: Int = 500
    val HEIGHT: Int = 500

    override def start(): IO[Unit] = for {
      _ <- gameFrame.setSize(WIDTH, HEIGHT)
      _ <- gameFrame.setResizable(false)
      _ <- gameFrame.setVisible(true)
    } yield ()

    override def dispose(): IO[Unit] = gameFrame.dispose()
  }

}