package it.amongsus.view.frame

import java.awt.BorderLayout
import cats.effect.IO
import it.amongsus.core.util.GameEnd.{CrewmateCrew, ImpostorCrew, Lost, Win}
import it.amongsus.core.util.GameEnd
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
  def apply(gameEnd: GameEnd): WinFrame = new WinFrameImpl(gameEnd)
  /**
   * The Frame that manages the winning
   */
  private class WinFrameImpl(gameEnd: GameEnd) extends WinFrame() {

    final val spaceDimension10: Int = 10
    final val spaceDimension60: Int = 60
    val frame = new JFrameIO(new JFrame("Among Sus - Winner"))
    val votePanel: JPanelIO = JPanelIO().unsafeRunSync()
    val WIDTH: Int = 2000
    val HEIGHT: Int = 800
    val role: String = gameEnd match {
      case Win(_,_) => gameEnd.crew match {
        case _: CrewmateCrew => "Crewmate"
        case _: ImpostorCrew => "Impostor"
      }
      case Lost(_,_) => gameEnd.crew match {
        case _: CrewmateCrew => "Crewmate"
        case _: ImpostorCrew => "Impostor"
      }
    }
    var teamWinner: String = ""

    override def start(): IO[Unit] = {
      for {
        menuBorder <- BorderFactoryIO.emptyBorderCreated(spaceDimension10,
          spaceDimension10, spaceDimension10, spaceDimension10)
        _ <- votePanel.setBorder(menuBorder)
        _ <- votePanel.setLayout(new BorderLayout())

        text <- JLabelIO()
        borderText <- BorderFactoryIO.emptyBorderCreated(0, spaceDimension60, 0, 0)
        _ <- text.setBorder(borderText)

        _ <- IO(for (user <- gameEnd.players.indices) {
          this.teamWinner = this.teamWinner + " " + gameEnd.players(user).username
        })
        _ <- text.setText("The Winning Team is: " + role + "\n and the Component Team is/are: \n" + teamWinner)

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