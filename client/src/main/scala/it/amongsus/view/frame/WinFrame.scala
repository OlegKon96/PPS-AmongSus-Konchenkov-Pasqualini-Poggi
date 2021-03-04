package it.amongsus.view.frame

import java.awt.BorderLayout
import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.core.entities.player.{Crewmate, Impostor, Player}
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