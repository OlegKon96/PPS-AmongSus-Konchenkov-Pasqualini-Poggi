package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.view.swingio.{BorderFactoryIO, JButtonIO, JFrameIO, JLabelIO, JPanelIO, JTextFieldIO}

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
    val lobbyView : LobbyFrame = LobbyFrame(this)
    val WIDTH: Int = 500
    val HEIGHT: Int = 250

    override def start(): IO[Unit] =
      for {
        menuPanel <- JPanelIO()
        menuBorder <- BorderFactoryIO.emptyBorderCreated(10, 10, 10, 10)
        _ <- menuPanel.setBorder(menuBorder)
        _ <- menuPanel.setLayout(new BorderLayout())
        titlePanel <- JPanelIO()
        titleBorder <- BorderFactoryIO.emptyBorderCreated(25, 200, 10, 200)
        _ <- titlePanel.setBorder(titleBorder)
        _ <- titlePanel.setLayout(new BorderLayout())
        title <- JLabelIO()
        _ <- title.setText("AMONG SUS")
        _ <- titlePanel.add(title,BorderLayout.CENTER)
        _ <- menuPanel.add(titlePanel,BorderLayout.NORTH)
        inputPanel <- JPanelIO()
        _ <- inputPanel.setLayout(new GridLayout(5,1))
        nameField <- JTextFieldIO()
        _ <- inputPanel.add(nameField)
        joinPublic <- JButtonIO("Partecipa ad una partita pubblica")
        _ <- joinPublic.addActionListener(for {
          _ <- frame.dispose()
          _ <- lobbyView.start()
        } yield())
        _ <- inputPanel.add(joinPublic)
        startPrivate <- JButtonIO("Crea una partita privata")
        _ <- inputPanel.add(startPrivate)
        codeField <- JTextFieldIO()
        _ <- inputPanel.add(codeField)
        joinPrivate <- JButtonIO("Partecipa ad una partita privata")
        _ <- inputPanel.add(joinPrivate)
        _ <- menuPanel.add(inputPanel,BorderLayout.SOUTH)
        cp <- frame.contentPane()
        _ <- cp.add(menuPanel)
        _ <- frame.setResizable(false)
        _ <- frame.setSize(WIDTH, HEIGHT)
        _ <- frame.setVisible(true)
      } yield ()
  }

}