package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.view.swingio._
import java.awt.BorderLayout
import javax.swing.JFrame

/**
 *
 */
trait LobbyFrame {
  /**
   *
   * @param numPlayers the number of the players
   * @param code the code of the lobby
   * @return
   */
  def start(numPlayers: Int, code : String): IO[Unit]

  /**
   *
   * @return
   */
  def toMenu : IO[Unit]

  /**
   *
   * @return
   */
  def toGame : IO[Unit]

  /**
   *
   * @param numPlayers the number of the players
   */
  def updatePlayers(numPlayers : Int) : IO[Unit]
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
    val gameView : GameFrame = GameFrame(Option(guiRef),menuView)
    val WIDTH: Int = 400
    val HEIGHT: Int = 300
    val players = JLabelIO().unsafeRunSync()

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
          _ <- players.setText("ASDASDASD")
        } yield ())
        _ <- topPanel.add(back, BorderLayout.WEST)
        _ <- IO(println("frame1:" + numPlayers))
        _ <- players.setText("Partecipanti" + numPlayers.toString + "/10")
        _ <- IO(println("frame2:" + numPlayers))
        _ <- controlPanel.add(players, BorderLayout.EAST)
        mainPanel <- JPanelIO()
        _ <- mainPanel.setLayout(new BorderLayout())
        mainBorder <- BorderFactoryIO.emptyBorderCreated(20, 160, 50, 20)
        _ <- mainPanel.setBorder(mainBorder)
        codeLabel <- JLabelIO(if (code == "") "Attendi altri giocatori" else "Il tuo codice è : " + code)
        _ <- mainPanel.add(codeLabel, BorderLayout.CENTER)
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

    override def updatePlayers(numPlayers: Int): IO[Unit] = for{
      _ <- IO(println("frame3:" + numPlayers))
      _ <- players.setText("Partecipanti" + numPlayers.toString + "/10")
    }yield()

    override def toGame: IO[Unit] = for {
      _ <- lobbyFrame.dispose()
      _ <- gameView start()
    } yield()
  }
}