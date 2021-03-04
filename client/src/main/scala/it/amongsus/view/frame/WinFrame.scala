package it.amongsus.view.frame

import java.awt.BorderLayout

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.core.entities.player.{Crewmate, Impostor, Player}
import it.amongsus.view.frame.WinFrame.WinFrameImpl
import it.amongsus.view.swingio.{BorderFactoryIO, JFrameIO, JLabelIO, JPanelIO}
import javax.swing.JFrame

/**
 * Trait of the Win Frame that manages the Win Screen
 */
trait WinFrame extends Frame {
  /**
   * Open the Vote Frame to display the winner team
   *
   * @return
   */
  def start(): IO[Unit]
}

object WinFrame {
  def apply(guiRef: Option[ActorRef], winnerCrew: Player): WinFrame =
    new WinFrameImpl(guiRef: Option[ActorRef], winnerCrew: Player)

  /**
   * The Frame that manages the winning
   *
   * @param guiRef ActorRef that is responsible to receiving and send all the messages about winning
   */
  private class WinFrameImpl(guiRef: Option[ActorRef], winnerCrew: Player) extends WinFrame() {
    /**
     * Open the Vote Frame to display the winner team
     *
     * @return
     */
    override def start(): IO[Unit] = ???

    override def dispose(): IO[Unit] = ???
  }

    override def dispose(): IO[Unit] = for {
      _ <- frame.dispose()
    } yield ()
  }
}