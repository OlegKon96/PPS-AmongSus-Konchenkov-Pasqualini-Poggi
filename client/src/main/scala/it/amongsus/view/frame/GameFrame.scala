package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.core.Drawable
import it.amongsus.core.map.{Collectionable, DeadBody, Tile}
import it.amongsus.core.util.ActionType.{EmergencyAction, KillAction, ReportAction, SabotageAction, VentAction}
import it.amongsus.view.actor.UiActorGameMessages.{MyCharMovedUi, UiActionTypeUi}
import it.amongsus.view.actor.UiActorLobbyMessages.PlayerCloseUi
import it.amongsus.view.controller.Keyboard
import it.amongsus.view.panel.GamePanel
import it.amongsus.view.swingio.{JButtonIO, JFrameIO, JPanelIO}

import java.awt.event.{WindowAdapter, WindowEvent}
import java.awt.{BorderLayout, GridLayout}
import it.amongsus.core.player.{Crewmate, Impostor, Player}
import it.amongsus.core.util.{ActionType, Direction}

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
  def setButtonState(button: ActionType, boolean: Boolean) : IO[Unit]
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
  def movePlayer(direction: Direction)
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

    final val GAME_FRAME_WIDTH : Int = 1230
    final val GAME_PANEL_WIDTH : Int = 1080
    final val GAME_HEIGHT : Int = 775
    final val BUTTON_PANEL_WIDTH : Int = 150
    final val IMPOSTOR_ROWS_NUMBER : Int = 5
    final val CREWMATE_ROWS_NUMBER : Int = 2
    final val COLS_NUMBER : Int = 1

    val gameFrame = new JFrameIO(new JFrame("Among Sus"))
    val gamePanel: GamePanel = GamePanel(map,myChar,players,collectionables,Seq.empty)
    val reportButton : JButtonIO = JButtonIO("Report").unsafeRunSync()
    val killButton: JButtonIO = JButtonIO("Kill").unsafeRunSync()
    val emergencyButton: JButtonIO = JButtonIO("Call Emergency").unsafeRunSync()
    val ventButton: JButtonIO = JButtonIO("Vent").unsafeRunSync()
    val sabotageButton: JButtonIO = JButtonIO("Sabotage").unsafeRunSync()

    override def start(): IO[Unit] = for {
      _ <- gameFrame.addKeyListener(Keyboard(this))
      _ <- IO(gamePanel.setSize(GAME_PANEL_WIDTH,GAME_HEIGHT))
      buttonPanel <- myChar match {
        case _: Crewmate => createCrewmateButton()
        case _: Impostor => createImpostorButton()
      }
      cp <- gameFrame.contentPane()
      _ <- cp.add(new JPanelIO(gamePanel), BorderLayout.CENTER)
      _ <- cp.add(buttonPanel, BorderLayout.EAST)
      _ <- gameFrame.setSize(GAME_FRAME_WIDTH, GAME_HEIGHT)
      _ <- gameFrame.setLocationRelativeToInvokingAndWaiting(gameFrame.component)
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

    override def movePlayer(direction : Direction): Unit = guiRef.get ! MyCharMovedUi(direction)

    override def updatePlayers(myChar: Player,
                               players: Seq[Player],
                               collectionables : Seq[Collectionable],
                               deadBodies : Seq[DeadBody]): Unit =
      gamePanel.updateGame(myChar,players,collectionables,deadBodies)

    private def createCrewmateButton() : IO[JPanelIO] = for {
      crewmateButtonPanel <- JPanelIO()
      _ <- crewmateButtonPanel.setLayout(new GridLayout(CREWMATE_ROWS_NUMBER, COLS_NUMBER))
      _ <- reportButton.setFocusable(false)
      _ <- reportButton.setEnabled(false)
      _ <- reportButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiActionTypeUi(ReportAction()))
      } yield ())
      _ <- crewmateButtonPanel.add(reportButton)
      _ <- emergencyButton.setFocusable(false)
      _ <- emergencyButton.setEnabled(false)
      _ <- emergencyButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiActionTypeUi(EmergencyAction()))
      } yield ())
      _ <- crewmateButtonPanel.add(emergencyButton)
      _ <- crewmateButtonPanel.setSize(BUTTON_PANEL_WIDTH,GAME_HEIGHT)
    } yield (crewmateButtonPanel)

    private def createImpostorButton() : IO[JPanelIO] = for {
      impostorButtonPanel <- JPanelIO()
      _ <- impostorButtonPanel.setLayout(new GridLayout(IMPOSTOR_ROWS_NUMBER, COLS_NUMBER))
      _ <- ventButton.setFocusable(false)
      _ <- ventButton.setEnabled(false)
      _ <- ventButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiActionTypeUi(VentAction()))
      } yield ())
      _ <- impostorButtonPanel.add(ventButton)
      _ <- sabotageButton.setFocusable(false)
      _ <- sabotageButton.setEnabled(false)
      _ <- sabotageButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiActionTypeUi(SabotageAction()))
      } yield ())
      _ <- impostorButtonPanel.add(sabotageButton)
      _ <- reportButton.setFocusable(false)
      _ <- reportButton.setEnabled(false)
      _ <- reportButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiActionTypeUi(ReportAction()))
      } yield ())
      _ <- impostorButtonPanel.add(reportButton)
      _ <- emergencyButton.setFocusable(false)
      _ <- emergencyButton.setEnabled(false)
      _ <- emergencyButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiActionTypeUi(EmergencyAction()))
      } yield ())
      _ <- impostorButtonPanel.add(emergencyButton)
      _ <- killButton.setFocusable(false)
      _ <- killButton.setEnabled(false)
      _ <- killButton.addActionListener(for {
        _ <- IO(guiRef.get ! UiActionTypeUi(KillAction()))
      } yield ())
      _ <- impostorButtonPanel.add(killButton)
      _ <- impostorButtonPanel.setSize(BUTTON_PANEL_WIDTH,GAME_HEIGHT)
    } yield(impostorButtonPanel)

    override def setButtonState(action: ActionType, boolean: Boolean): IO[Unit] = {
      myChar match {
        case _: Impostor => action match {
          case _: KillAction => for {
            _ <- killButton.setText("Kill")
            _ <- killButton.setEnabled(boolean)
          } yield()
          case _: ReportAction => reportButton.setEnabled(boolean)
          case _: EmergencyAction => emergencyButton.setEnabled(boolean)
          case _: VentAction => ventButton.setEnabled(boolean)
          case _: SabotageAction => for {
            _ <- sabotageButton.setText("Sabotage")
            _ <- sabotageButton.setEnabled(boolean)
          } yield()
        }
        case _: Crewmate => action match {
          case _: ReportAction => reportButton.setEnabled(boolean)
          case _: EmergencyAction => emergencyButton.setEnabled(boolean)
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