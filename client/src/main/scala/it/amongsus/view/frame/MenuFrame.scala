package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.view.swingio.{BorderFactoryIO, JFrameIO, JPanelIO}

import java.awt.{BorderLayout, GridLayout}
import javax.swing.JFrame

trait MenuFrame {
  def start(): IO[Unit]
}

object MenuFrame {

  def apply(guiRef: Option[ActorRef]): MenuFrame = new MenuFrameImpl(guiRef)

  /** Frame to start the game.
   *
   * @param guiRef ActorRef responsible for receiving and send all the messages about lobby management
   */
  private class MenuFrameImpl(guiRef: Option[ActorRef]) extends MenuFrame() {

    val frame = new JFrameIO(new JFrame("Among Sus"))
    val WIDTH: Int = 500
    val HEIGHT: Int = 250

    override def start(): IO[Unit] =
      for {
        _ <- frame.setSize(WIDTH, HEIGHT)
        _ <- frame.setVisible(true)
        menuPanel <- JPanelIO()
        menuBorder <- BorderFactoryIO.emptyBorderCreated(10, 10, 10, 10)
        _ <- menuPanel.setBorder(menuBorder)
        _ <- menuPanel.setLayout(new BorderLayout())
      } yield ()
  }

}