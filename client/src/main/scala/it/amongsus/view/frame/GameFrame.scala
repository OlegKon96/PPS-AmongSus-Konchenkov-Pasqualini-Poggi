package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.core.entities.map.{Collectionable, DeadBody, Tile}
import it.amongsus.core.entities.player.{Crewmate, Impostor, Player}
import it.amongsus.core.entities.util.ButtonType.{EmergencyButton, KillButton, ReportButton, SabotageButton, VentButton}
import it.amongsus.core.entities.util.{ButtonType, Movement}
import it.amongsus.view.actor.UiActorGameMessages.{MyCharMovedUi, UiButtonPressedUi}
import it.amongsus.view.controller.Keyboard
import it.amongsus.view.panel.GamePanel
import it.amongsus.view.swingio.{JButtonIO, JFrameIO, JPanelIO}

import java.awt.{BorderLayout, GridLayout}
import javax.swing.JFrame

/**
 *
 */
trait GameFrame extends Frame {
  /**
   *
   * @return
   */
  def start(): IO[Unit]

  def map: Array[Array[Tile]]

  def myChar : Player

  def players : Seq[Player]

  def collectionables : Seq[Collectionable]

  def movePlayer(direction: Movement): Unit

  def updatePlayers(myChar: Player, players: Seq[Player], collectionables : Seq[Collectionable],
                    deadBodies : Seq[DeadBody]) :Unit

  def enableButton(button: ButtonType, boolean: Boolean) : IO[Unit]
}

object GameFrame {
  def apply(guiRef: Option[ActorRef],
            map : Array[Array[Tile]],
            myChar: Player,
            players : Seq[Player],
            collectionables : Seq[Collectionable]): GameFrame = new GameFrameImpl(guiRef,map,myChar,players,collectionables)

  private class GameFrameImpl(guiRef: Option[ActorRef],
                              override val map : Array[Array[Tile]],
                              override val myChar : Player,
                              override val players : Seq[Player],
                              override val collectionables : Seq[Collectionable]) extends GameFrame {
    val gameFrame = new JFrameIO(new JFrame("Among Sus"))
    val WIDTH: Int = 1230
    val HEIGHT: Int = 775
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
      _ <- gameFrame.setSize(WIDTH, HEIGHT)
      _ <- gameFrame.setResizable(false)
      _ <- gameFrame.setVisible(true)
    } yield ()

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

    override def dispose(): IO[Unit] = gameFrame.dispose()

    override def movePlayer(direction: Movement): Unit = guiRef.get ! MyCharMovedUi(direction)

    override def updatePlayers(myChar: Player,
                               players: Seq[Player],
                               collectionables : Seq[Collectionable],
                               deadBodies : Seq[DeadBody]): Unit =
      gamePanel.updateGame(myChar,players,collectionables,deadBodies)

    override def enableButton(button: ButtonType, boolean: Boolean): IO[Unit] = {
      myChar match {
        case _: Impostor => button match {
          case _: KillButton => killButton.setEnabled(boolean)
          case _: ReportButton => reportButton.setEnabled(boolean)
          case _: EmergencyButton => emergencyButton.setEnabled(boolean)
          case _: VentButton => ventButton.setEnabled(boolean)
          case _: SabotageButton => sabotageButton.setEnabled(boolean)
        }
        case _: Crewmate => button match {
          case _: ReportButton => reportButton.setEnabled(boolean)
          case _: EmergencyButton => emergencyButton.setEnabled(boolean)
        }
      }
    }
  }

}