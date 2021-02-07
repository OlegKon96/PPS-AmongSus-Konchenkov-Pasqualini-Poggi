package it.amongsus.view.frame

import cats.effect.IO
import it.amongsus.view.swingio.{BorderFactoryIO, JButtonIO, JFrameIO, JLabelIO, JPanelIO}

import java.awt.{BorderLayout, GridLayout}
import javax.swing.JFrame

trait LobbyFrame {
  def start(): IO[Unit]
}

object LobbyFrame {

  def apply(menuView: MenuFrame): LobbyFrame = new LobbyFrameImpl(menuView)

  private class LobbyFrameImpl(menuView: MenuFrame) extends LobbyFrame {

    val lobbyFrame = new JFrameIO(new JFrame("Among Sus"))
    val WIDTH: Int = 400
    val HEIGHT: Int = 300

    def start(): IO[Unit] =
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
          _ <- lobbyFrame.dispose()
          _ <- menuView.start()
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
  }

}