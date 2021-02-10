package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.view.swingio._

import java.awt.{BorderLayout, GridLayout}
import javax.swing.JFrame

trait LobbyFrame {
  def start(numPlayers: Int, code : String): IO[Unit]

  def toMenu : IO[Unit]
}

object LobbyFrame {

  def apply(menuView: MenuFrame, guiRef : ActorRef): LobbyFrame = new LobbyFrameImpl(menuView,guiRef)

  /**
   * The Frame that manages the Lobby
   *
   * @param menuView The Menu' View of the Game
   */
  private class LobbyFrameImpl(menuView: MenuFrame,guiRef: ActorRef) extends LobbyFrame {

    val lobbyFrame = new JFrameIO(new JFrame("Among Sus"))
    val WIDTH: Int = 400
    val HEIGHT: Int = 300

    def start(numPlayers: Int, code : String): IO[Unit] =
      for {
        lobbyPanel <- JPanelIO()
        _ <- lobbyPanel.setLayout(new BorderLayout())
        topPanel <- JPanelIO()
        _ <- topPanel.setLayout(new BorderLayout())
        controlPanel <- JPanelIO()
        _ <- controlPanel.setLayout(new BorderLayout())
        basicBorder <- BorderFactoryIO.emptyBorderCreated(10, 10, 10, 10)
        _ <- controlPanel.setBorder(basicBorder)
        _ <- topPanel.setBorder(basicBorder)
        back <- JButtonIO("<")
        _ <- back.addActionListener(for {
          _ <- toMenu
        } yield ())
        _ <- topPanel.add(back, BorderLayout.WEST)
        players <- JLabelIO("Partecipanti 9/10")
        _ <- controlPanel.add(players, BorderLayout.EAST)
        mainPanel <- JPanelIO()
        _ <- mainPanel.setLayout(new GridLayout(2, 1))
        mainBorder <- BorderFactoryIO.emptyBorderCreated(20, 160, 50, 20)
        _ <- mainPanel.setBorder(mainBorder)
        codeLabel <- JLabelIO("Your code is:")
        code <- JLabelIO("1543512")
        _ <- mainPanel.add(codeLabel)
        _ <- mainPanel.add(code)
        _ <- lobbyPanel.add(topPanel, BorderLayout.NORTH)
        _ <- lobbyPanel.add(mainPanel, BorderLayout.CENTER)
        _ <- lobbyPanel.add(controlPanel, BorderLayout.SOUTH)
        cp <- lobbyFrame.contentPane()
        _ <- lobbyFrame.setSize(WIDTH, HEIGHT)
        _ <- cp.add(lobbyPanel)
        _ <- lobbyFrame.setVisible(true)
        _ <- lobbyFrame.setResizable(false)

      } yield ()

    override def toMenu: IO[Unit] = for {
      _ <- lobbyFrame.dispose()
      _ <- menuView start()
    } yield()
  }
}