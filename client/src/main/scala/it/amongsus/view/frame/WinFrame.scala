package it.amongsus.view.frame

import java.awt.BorderLayout
import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.core.entities.player.{Crewmate, Impostor, Player}
import it.amongsus.view.swingio.{BorderFactoryIO, JFrameIO, JLabelIO, JPanelIO}
import javax.swing.JFrame

/**
 * Trait of the Win Frame that manages the Win Screen
 */
trait WinFrame extends Frame {
  /**
   * Open the Vote Frame to display the winner team
   *
   * @return
   */
  def start(): IO[Unit]
}

object WinFrame {
  def apply(guiRef: Option[ActorRef], winnerCrew: Player): WinFrame =
    new WinFrameImpl(guiRef: Option[ActorRef], winnerCrew: Player)

  /**
   * The Frame that manages the winning
   *
   * @param guiRef ActorRef that is responsible to receiving and send all the messages about winning
   */
  private class WinFrameImpl(guiRef: Option[ActorRef], winnerCrew: Player) extends WinFrame() {

    final val spaceDimension10: Int = 10
    final val spaceDimension60: Int = 60
    val frame = new JFrameIO(new JFrame("Among Sus - Winner"))
    val votePanel: JPanelIO = JPanelIO().unsafeRunSync()
    val WIDTH: Int = 1000
    val HEIGHT: Int = 800

    override def start(): IO[Unit] = {
      val role = winnerCrew match {
        case _: Crewmate => "Crewmate"
        case _: Impostor => "Impostor"
      }
      for {
        menuBorder <- BorderFactoryIO.emptyBorderCreated(spaceDimension10,
          spaceDimension10, spaceDimension10, spaceDimension10)
        _ <- votePanel.setBorder(menuBorder)
        _ <- votePanel.setLayout(new BorderLayout())

        text <- JLabelIO()
        borderText <- BorderFactoryIO.emptyBorderCreated(0, spaceDimension60, 0, 0)
        _ <- text.setBorder(borderText)
        _ <- text.setText("The Winning Team is: " + role)

        _ <- votePanel.add(text, BorderLayout.CENTER)

        cp <- frame.contentPane()
        _ <- cp.add(votePanel)
        _ <- frame.setResizable(false)
        _ <- frame.setTitle("Among Sus - Results")
        _ <- frame.setSize(WIDTH/3, HEIGHT/3)
        _ <- frame.setVisible(true)
      } yield ()
    }

    override def dispose(): IO[Unit] = for {
      _ <- frame.dispose()
    } yield ()
  }
}