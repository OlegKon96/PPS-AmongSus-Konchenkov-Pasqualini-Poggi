package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.core.entities.map.{Collectionable, DeadBody, Tile}
import it.amongsus.core.entities.player.{Crewmate, Impostor}
import it.amongsus.core.entities.player.Player
import it.amongsus.core.entities.util.{ButtonType, Movement}
import it.amongsus.core.entities.util.ButtonType.{EmergencyButton, KillButton, ReportButton, SabotageButton, VentButton}
import it.amongsus.view.actor.UiActorGameMessages.{MyCharMovedUi, UiButtonPressedUi}
import it.amongsus.view.actor.UiActorLobbyMessages.PlayerCloseUi
import it.amongsus.view.controller.Keyboard
import it.amongsus.view.panel.GamePanel
import it.amongsus.view.swingio.{JButtonIO, JFrameIO, JPanelIO}
import java.awt.event.{WindowAdapter, WindowEvent}
import java.awt.{BorderLayout, GridLayout}

import it.amongsus.core.entities.Drawable
import javax.swing.JFrame

/**
 * Trait that manages the Game Frame of the game
 */
trait GameFrame extends Frame {
  /**
   * Method that updates the player
   *
   * @param myChar my character of the game
   * @param players of the game
   * @param collectionables of the game
   * @param deadBodies of the game
   */
  def updatePlayers(myChar: Player, players: Seq[Player], collectionables : Seq[Collectionable],
                    deadBodies : Seq[DeadBody]) :Unit
  /**
   * Method to start the Game Frame
   *
   * @return
   */
  def start(): IO[Unit]
  /**
   *  The Map of the game
   *
   * @return
   */
  def map: Array[Array[Drawable[Tile]]]
  /**
   * My players of the game
   *
   * @return
   */
  def myChar: Player
  /**
   * Method that manages the buttons of the game
   *
   * @param button to manages
   * @param boolean enable or disable
   * @return
   */
  def enableButton(button: ButtonType, boolean: Boolean) : IO[Unit]
  /**
   * Sequence of the players of the game
   *
   * @return
   */
  def players : Seq[Player]
  /**
   * Method that manages the coin of the game
   *
   * @return
   */
  def collectionables : Seq[Collectionable]
  /**
   * Method that moves the player
   *
   * @param direction to move on
   */
  def movePlayer(direction: Movement): Unit
  /**
   * Method to update the kill button
   *
   * @param seconds to wait before activate button
   * @return
   */
  def updateKillButton(seconds : Long) : IO[Unit]
  /**
   * Method to update the sabotage button
   *
   * @param seconds to wait before activate button
   * @return
   */
  def updateSabotageButton(seconds : Long) : IO[Unit]
}

object GameFrame {
  def apply(guiRef: Option[ActorRef],
            map : Array[Array[Drawable[Tile]]],
            myChar: Player,
            players : Seq[Player],
            collectionables : Seq[Collectionable]): GameFrame =
    new GameFrameImpl(guiRef,map,myChar,players,collectionables)

  private class GameFrameImpl(guiRef: Option[ActorRef],
                              override val map : Array[Array[Drawable[Tile]]],
                              override val myChar : Player,
                              override val players : Seq[Player],
                              override val collectionables : Seq[Collectionable]) extends GameFrame {
    val gameFrame = new JFrameIO(new JFrame("Among Sus"))
    val gamePanel: GamePanel = GamePanel(map,myChar,players,collectionables,Seq.empty)
    val reportButton : JButtonIO = JButtonIO("Report").unsafeRunSync()
    val killButton: JButtonIO = JButtonIO("Kill").unsafeRunSync()
    val emergencyButton: JButtonIO = JButtonIO("Call Emergency").unsafeRunSync()
    val ventButton: JButtonIO = JButtonIO("Vent").unsafeRunSync()
    val sabotageButton: JButtonIO = JButtonIO("Sabotage").unsafeRunSync()

    override def start(): IO[Unit] = for {
      _ <- gameFrame.addKeyListener(Keyboard(this))
      _ <- IO(gamePanel.setSize(1080,775))
      buttonPanel <- myChar match {
        case _: Crewmate => createCrewmateButton()
        case _: Impostor => createImpostorButton()
      }
      cp <- gameFrame.contentPane()
      _ <- cp.add(new JPanelIO(gamePanel), BorderLayout.CENTER)
      _ <- cp.add(buttonPanel, BorderLayout.EAST)
      _ <- gameFrame.setSize(1230, 775)
      _ <- gameFrame.setResizable(false)
      _ <- gameFrame.setVisible(true)
      _ <- gameFrame.requestFocusInWindow()
      _ <- gameFrame.addWindowListener(new WindowAdapter {
        override def windowClosing(e: WindowEvent): Unit = {
          guiRef.get ! PlayerCloseUi()
        }
      })
      _ <- gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
    } yield ()

    override def dispose(): IO[Unit] = gameFrame.dispose()

    override def movePlayer(direction : Movement): Unit = guiRef.get ! MyCharMovedUi(direction)

    override def updatePlayers(myChar: Player,
                               players: Seq[Player],
                               collectionables : Seq[Collectionable],
                               deadBodies : Seq[DeadBody]): Unit =
      gamePanel.updateGame(myChar,players,collectionables,deadBodies)

    private def createCrewmateButton() : IO[JPanelIO] = for {
      crewmateButtonPanel <- JPanelIO()
      _ <- crewmateButtonPanel.setLayout(new GridLayout(2, 1))
      _ <- reportButton.setFocusable(false)
      _ <- reportButton.setEnabled(false)
      _ <- reportButton.setSize(150,50)
      _ <- reportButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiButtonPressedUi(ReportButton()))
      } yield ())
      _ <- crewmateButtonPanel.add(reportButton)
      _ <- emergencyButton.setFocusable(false)
      _ <- emergencyButton.setEnabled(false)
      _ <- emergencyButton.setSize(150,50)
      _ <- emergencyButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiButtonPressedUi(EmergencyButton()))
      } yield ())
      _ <- crewmateButtonPanel.add(emergencyButton)
      _ <- crewmateButtonPanel.setSize(150,775)
    } yield (crewmateButtonPanel)

    private def createImpostorButton() : IO[JPanelIO] = for {
      impostorButtonPanel <- JPanelIO()
      _ <- impostorButtonPanel.setLayout(new GridLayout(5, 1))
      _ <- ventButton.setFocusable(false)
      _ <- ventButton.setEnabled(false)
      _ <- ventButton.setSize(150,50)
      _ <- ventButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiButtonPressedUi(VentButton()))
      } yield ())
      _ <- impostorButtonPanel.add(ventButton)
      _ <- sabotageButton.setFocusable(false)
      _ <- sabotageButton.setEnabled(false)
      _ <- sabotageButton.setSize(150,50)
      _ <- sabotageButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiButtonPressedUi(SabotageButton()))
      } yield ())
      _ <- impostorButtonPanel.add(sabotageButton)
      _ <- reportButton.setFocusable(false)
      _ <- reportButton.setEnabled(false)
      _ <- reportButton.setSize(150,50)
      _ <- reportButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiButtonPressedUi(ReportButton()))
      } yield ())
      _ <- impostorButtonPanel.add(reportButton)
      _ <- emergencyButton.setFocusable(false)
      _ <- emergencyButton.setEnabled(false)
      _ <- emergencyButton.setSize(150,50)
      _ <- emergencyButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiButtonPressedUi(EmergencyButton()))
      } yield ())
      _ <- impostorButtonPanel.add(emergencyButton)
      _ <- killButton.setFocusable(false)
      _ <- killButton.setEnabled(false)
      _ <- killButton.setSize(150,50)
      _ <- killButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiButtonPressedUi(KillButton()))
      } yield ())
      _ <- impostorButtonPanel.add(killButton)
      _ <- impostorButtonPanel.setSize(150,775)
    } yield(impostorButtonPanel)

    override def enableButton(button: ButtonType, boolean: Boolean): IO[Unit] = {
      myChar match {
        case _: Impostor => button match {
          case _: KillButton => for {
            _ <- killButton.setText("Kill")
            _ <- killButton.setEnabled(boolean)
          } yield()
          case _: ReportButton => reportButton.setEnabled(boolean)
          case _: EmergencyButton => emergencyButton.setEnabled(boolean)
          case _: VentButton => ventButton.setEnabled(boolean)
          case _: SabotageButton => for {
            _ <- sabotageButton.setText("Sabotage")
            _ <- sabotageButton.setEnabled(boolean)
          } yield()
        }
        case _: Crewmate => button match {
          case _: ReportButton => reportButton.setEnabled(boolean)
          case _: EmergencyButton => emergencyButton.setEnabled(boolean)
        }
      }
    }

    override def updateKillButton(seconds: Long): IO[Unit] = for {
      _ <- killButton.setText("Countdown: " + seconds.toString)
    } yield()

    override def updateSabotageButton(seconds: Long): IO[Unit] = for {
      _ <- sabotageButton.setText("Countdown: " + seconds.toString)
    } yield()
  }
}