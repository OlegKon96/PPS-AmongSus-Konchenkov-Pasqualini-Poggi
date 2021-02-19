package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.core.entities.map.{Collectionable, Tile}
import it.amongsus.core.entities.player.Player
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

  def players : Seq[Player]

  def collectionables : Seq[Collectionable]
}

object GameFrame {
  def apply(guiRef: Option[ActorRef],
            map : Array[Array[Tile]],
            players : Seq[Player],
            collectionables : Seq[Collectionable]): GameFrame = new GameFrameImpl(guiRef,map,players,collectionables)

  private class GameFrameImpl(guiRef: Option[ActorRef],
                              override val map : Array[Array[Tile]],
                              override val players : Seq[Player],
                              override val collectionables : Seq[Collectionable]) extends GameFrame {
    val gameFrame = new JFrameIO(new JFrame("Among Sus"))
    val WIDTH: Int = 1230
    val HEIGHT: Int = 775
    val gamePanel: GamePanel = GamePanel(map,players,collectionables)
    val reportButton : JButtonIO = JButtonIO("Report").unsafeRunSync()
    val killButton: JButtonIO = JButtonIO("Kill").unsafeRunSync()
    val emergencyButton: JButtonIO = JButtonIO("Call Emergency").unsafeRunSync()
    val ventButton: JButtonIO = JButtonIO("Vent").unsafeRunSync()

    override def start(): IO[Unit] = for {
      _ <- gameFrame.addKeyListener(Keyboard(this))
      _ <- IO(gamePanel.setSize(1080,775))
      buttonPanel <- createButtonPanel()
      cp <- gameFrame.contentPane()
      _ <- cp.add(new JPanelIO(gamePanel), BorderLayout.CENTER)
      _ <- cp.add(buttonPanel, BorderLayout.EAST)
      _ <- gameFrame.setSize(WIDTH, HEIGHT)
      _ <- gameFrame.setResizable(false)
      _ <- gameFrame.setVisible(true)
    } yield ()

    private def createButtonPanel() : IO[JPanelIO] = for {
      buttonPanel <- JPanelIO()
      _ <- buttonPanel.setLayout(new GridLayout(4, 1))
      _ <- ventButton.setFocusable(false)
      _ <- ventButton.setEnabled(false)
      _ <- ventButton.setSize(150,50)
      _ <- buttonPanel.add(ventButton)
      _ <- reportButton.setFocusable(false)
      _ <- reportButton.setEnabled(false)
      _ <- reportButton.setSize(150,50)
      _ <- buttonPanel.add(reportButton)
      _ <- emergencyButton.setFocusable(false)
      _ <- emergencyButton.setEnabled(false)
      _ <- emergencyButton.setSize(150,50)
      _ <- buttonPanel.add(emergencyButton)
      _ <- killButton.setFocusable(false)
      _ <- killButton.setEnabled(false)
      _ <- killButton.setSize(150,50)
      _ <- buttonPanel.add(killButton)
      _ <- buttonPanel.setSize(150,775)

    } yield(buttonPanel)

    override def dispose(): IO[Unit] = gameFrame.dispose()
  }

}