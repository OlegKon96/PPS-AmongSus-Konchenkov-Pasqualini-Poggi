package it.amongsus.view.frame

import java.awt.{BorderLayout, GridLayout}
import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.view.actor.UiActorGameMessages.PlayerReadyUi
import it.amongsus.view.actor.UiActorLobbyMessages.{LeaveLobbyUi, PlayerCloseUi}
import it.amongsus.view.swingio._
import java.awt.event.{WindowAdapter, WindowEvent}
import javax.swing.JFrame

/**
 * Trait that manages the Lobby Frame of the lobby system
 */
trait LobbyFrame extends Frame {
  /**
   * The lobby frame
   *
   * @return
   */
  def lobbyFrame: JFrameIO
  /**
   * Method that starts the lobby frame
   *
   * @param numPlayers number of players of the lobby
   * @param code of the lobby
   * @return
   */
  def start(numPlayers: Int, code: String): IO[Unit]
  /**
   * Method that updates the number of the player
   *
   * @param numPlayers the number of the players
   */
  def updatePlayers(numPlayers: Int): IO[Unit]
  /**
   * Method that shows a button on the GUI
   *
   * @param boolean true or false
   * @return
   */
  def showButton(boolean: Boolean): IO[Unit]
}

object LobbyFrame {
  def apply(guiRef: ActorRef, roomSize : Int): LobbyFrame = new LobbyFrameImpl(guiRef, roomSize)
  /**
   * The Frame that manages the Lobby
   */
  private class LobbyFrameImpl(guiRef: ActorRef, roomSize : Int) extends LobbyFrame {
    val lobbyFrame = new JFrameIO(new JFrame("Among Sus"))
    val WIDTH: Int = 400
    val HEIGHT: Int = 300
    val players: JLabelIO = JLabelIO().unsafeRunSync()
    val startButton : JButtonIO = JButtonIO("Inizia partita").unsafeRunSync()
    val size: Int = roomSize
    val backButton : JButtonIO = JButtonIO("<").unsafeRunSync()

    override def start(numPlayers: Int, code: String): IO[Unit] =
      for {
        lobbyPanel <- JPanelIO()
        _ <- lobbyPanel.setLayout(new GridLayout(4,1))
        topPanel <- JPanelIO()
        _ <- topPanel.setLayout(new BorderLayout())
        controlPanel <- JPanelIO()
        _ <- controlPanel.setLayout(new BorderLayout())
        basicBorder <- BorderFactoryIO.emptyBorderCreated(10, 10, 10, 10)
        _ <- controlPanel.setBorder(basicBorder)
        _ <- topPanel.setBorder(basicBorder)
        _ <- backButton.addActionListener(for {
          _ <- IO(guiRef ! LeaveLobbyUi())
        } yield ())
        _ <- topPanel.add(backButton, BorderLayout.WEST)
        _ <- players.setText("Partecipanti" + numPlayers.toString + "/" + size.toString)
        _ <- controlPanel.add(players, BorderLayout.EAST)
        mainPanel <- JPanelIO()
        _ <- mainPanel.setLayout(new BorderLayout())
        mainBorder <- BorderFactoryIO.emptyBorderCreated(0, 120, 0, 120)
        _ <- mainPanel.setBorder(mainBorder)
        codeLabel <- JLabelIO(if (code == "") "Attendi altri giocatori" else "Il tuo codice è : " + code)
        _ <- mainPanel.add(codeLabel, BorderLayout.CENTER)
        _ <- startButton.setVisible(false)
        startLabel <- JLabelIO("Attendi..")
        _ <- startLabel.setVisible(false)
        - <- startLabel.setBorder(mainBorder)
        _ <- startButton.addActionListener(for {
          _ <- IO(guiRef ! PlayerReadyUi())
          _ <- startLabel.setVisible(true)
          _ <- startButton.setEnabled(false)
        }yield())
        _ <- mainPanel.add(startButton, BorderLayout.SOUTH)
        _ <- lobbyPanel.add(topPanel)
        _ <- lobbyPanel.add(mainPanel)
        _ <- lobbyPanel.add(startLabel)
        _ <- lobbyPanel.add(controlPanel)
        cp <- lobbyFrame.contentPane()
        _ <- lobbyFrame.setSize(WIDTH, HEIGHT)
        _  <- lobbyFrame.setLocationRelativeToInvokingAndWaiting(null)
        _ <- cp.add(lobbyPanel)
        _ <- lobbyFrame.setVisible(true)
        _ <- lobbyFrame.setResizable(false)
        _ <- lobbyFrame.addWindowListener(new WindowAdapter {
          override def windowClosing(e: WindowEvent): Unit = {
            guiRef ! PlayerCloseUi()
          }
        })
        _ <- lobbyFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      } yield ()

    override def updatePlayers(numPlayers: Int): IO[Unit] =
      players.setText("Partecipanti" + numPlayers.toString + "/" + size.toString)

    override def dispose(): IO[Unit] = lobbyFrame.dispose()

    override def showButton(boolean: Boolean): IO[Unit] = for {
      _ <- backButton.setEnabled(false)
      _ <- startButton.setVisible(boolean)
    } yield()
  }
}