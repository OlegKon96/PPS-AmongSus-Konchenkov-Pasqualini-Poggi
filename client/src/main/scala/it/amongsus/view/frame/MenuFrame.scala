package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.view.swingio._
import java.awt.{BorderLayout, Color, GridLayout}

import it.amongsus.view.actor.UiActorLobbyMessages._
import java.awt.event.{WindowAdapter, WindowEvent}

import javax.swing.{JFrame, WindowConstants}

/**
 *
 */
trait MenuFrame extends Frame {
  /**
   *
   * @return
   */
  def start(): IO[Unit]

  /**
   * Returns the lobby code if it exist
   * @return lobby code
   */
  def code: String

  /**
   *
   * @param lobbyCode the code of the lobby
   */
  def saveCode(lobbyCode : String) : Unit

  /**
   *
   */
  def lobbyError() : Unit
}

object MenuFrame {

  def apply(guiRef: Option[ActorRef]): MenuFrame = new MenuFrameImpl(guiRef)

  /**
   * The Frame that starts the game
   *
   * @param guiRef ActorRef that is responsible to receiving and send all the messages about lobby
   */
  private class MenuFrameImpl(guiRef: Option[ActorRef]) extends MenuFrame() {

    val menuFrame = new JFrameIO(new JFrame("Among Sus"))
    val values : Seq[Int] = Seq(4,5,6,7,8,9,10)
    val WIDTH: Int = 500
    val HEIGHT: Int = 250
    var code : String = ""

    override def start(): IO[Unit] =
      for {
        _ <- IO(code = "")
        menuPanel <- JPanelIO()
        menuBorder <- BorderFactoryIO.emptyBorderCreated(10, 10, 10, 10)
        _ <- menuPanel.setBorder(menuBorder)
        _ <- menuPanel.setLayout(new BorderLayout())
        inputPanel <- JPanelIO()
        _ <- inputPanel.setLayout(new GridLayout(4, 2))
        nameLabel <- JLabelIO()
        _ <- nameLabel.setText("Inserisci il tuo nome")
        _ <- inputPanel.add(nameLabel)
        nameField <- JTextFieldIO()
        _ <- inputPanel.add(nameField)
        playersLabel <- JLabelIO()
        _ <- playersLabel.setText("Inserisci il numero di giocatori")
        _ <- inputPanel.add(playersLabel)
        comboBoxPlayers <- JComboBoxIO()
        _ <- IO(values.foreach(value => comboBoxPlayers.addItem(value).unsafeRunSync()))
        _ <- inputPanel.add(comboBoxPlayers)
        joinPublic <- JButtonIO("Partecipa ad una partita pubblica")
        _ <- joinPublic.addActionListener(for {
          nameText <- nameField.text
          _ <- IO(if (checkName(nameField)) {
            guiRef.get ! PublicGameSubmitUi(nameText, comboBoxPlayers.selectedItem.unsafeRunSync())
          })
        } yield ())
        _ <- inputPanel.add(joinPublic)
        startPrivate <- JButtonIO("Crea una partita privata")
        _ <- startPrivate.addActionListener(for {
          nameText <- nameField.text
          _ <- IO(if (checkName(nameField)) {
            guiRef.get ! CreatePrivateGameSubmitUi(nameText,comboBoxPlayers.selectedItem.unsafeRunSync())
          })
        } yield ())
        _ <- inputPanel.add(startPrivate)
        codeLabel <- JLabelIO()
        _ <- codeLabel.setText("Inserisci il codice della partita privata")
        _ <- inputPanel.add(codeLabel)
        codeField <- JTextFieldIO()
        _ <- inputPanel.add(codeField)
        joinPrivate <- JButtonIO("Partecipa ad una partita privata")
        _ <- joinPrivate.addActionListener(for {
          codeText <- codeField.text
          nameText <- nameField.text
          _ <- IO(if (checkName(nameField) && checkCode(codeField)) {
            code = codeText
            guiRef.get ! PrivateGameSubmitUi(nameText, code)
          })
        } yield ())

        _ <- menuPanel.add(inputPanel, BorderLayout.CENTER)
        _ <- menuPanel.add(joinPrivate, BorderLayout.SOUTH)
        cp <- menuFrame.contentPane()
        _ <- menuFrame.background(Color.LIGHT_GRAY)
        _ <- menuPanel.background(Color.LIGHT_GRAY)
        _ <- inputPanel.background(Color.LIGHT_GRAY)
        _ <- cp.add(menuPanel)
        _ <- menuFrame.setResizable(false)
        _ <- menuFrame.setTitle("Among Sus")
        _ <- menuFrame.setSize(WIDTH, HEIGHT)
        _ <- menuFrame.setVisible(true)
        _ <- menuFrame.addWindowListener(new WindowAdapter {
          override def windowClosing(e: WindowEvent): Unit = {
            guiRef.get ! PlayerCloseUi()
          }
        })
        _ <- menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      } yield ()

    private def checkName(nameField: JTextFieldIO): Boolean = nameField.text.unsafeRunSync() match {
      case "" => false
      case _ => true
    }

    private def checkCode(codeField : JTextFieldIO) : Boolean =  codeField.text.unsafeRunSync() match {
      case "" => false
      case _ => true
    }

    override def saveCode(lobbyCode: String): Unit = {
      code = lobbyCode
    }

    override def lobbyError(): Unit = {
      code = ""
    }

    override def dispose(): IO[Unit] = for {
      _ <- menuFrame.dispose()
    } yield ()
  }
}