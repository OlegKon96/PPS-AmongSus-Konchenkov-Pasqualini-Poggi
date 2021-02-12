package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.view.swingio.JFrameIO

import javax.swing.JFrame

trait GameFrame {
  def start(): IO[Unit]

  def toMenu: IO[Unit]
}

object GameFrame {

  def apply(guiRef: Option[ActorRef],menuFrame: MenuFrame): GameFrame = new GameFrameImpl(guiRef,menuFrame)

  private class GameFrameImpl(guiRef: Option[ActorRef],menuFrame: MenuFrame) extends GameFrame {
    val gameFrame = new JFrameIO(new JFrame("Among Sus"))

    override def start(): IO[Unit] = for {
      _ <- gameFrame.setSize(500, 500)
      _ <- gameFrame.setResizable(false)
      _ <- gameFrame.setVisible(true)
    } yield ()

    override def toMenu: IO[Unit] = for{
      _ <- gameFrame.dispose()
      _ <- menuFrame start
    } yield()
  }

}
