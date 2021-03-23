package it.amongsus.view.frame

import java.awt.{BorderLayout, Color, GridLayout}
import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.view.actor.UiActorLobbyMessages._
import it.amongsus.view.frame.Constants.MenuFrame.Numbers._
import it.amongsus.view.frame.Constants.MenuFrame.Strings._
import it.amongsus.view.swingio._

import java.awt.event.{WindowAdapter, WindowEvent}
import javax.swing.JFrame

/**
 * Trait the manages the Menu Frame of the game
 */
trait MenuFrame extends Frame {
  /**
   * Method that starts the Menu Frame
   *
   * @return
   */
  def start(): IO[Unit]
  /**
   * Method that saves the code of the lobby
   *
   * @param lobbyCode the code of the lobby
   */
  def saveCode(lobbyCode: String): Unit
  /**
   * Returns the lobby code if it exist
   *
   * @return
   */
  def code: String
  /**
   * Method that manages the error in the lobby
   */
  def lobbyError(): Unit
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
    var code: String = ""

    override def start(): IO[Unit] =
      for {
        _ <- IO(code = "")
        menuPanel <- JPanelIO()
        menuBorder <- BorderFactoryIO.emptyBorderCreated(BASIC_BORDER, BASIC_BORDER, BASIC_BORDER, BASIC_BORDER)
        _ <- menuPanel.setBorder(menuBorder)
        _ <- menuPanel.setLayout(new BorderLayout())
        inputPanel <- JPanelIO()
        _ <- inputPanel.setLayout(new GridLayout(ROWS_NUMBER, MENU_COLS_NUMBER))
        nameLabel <- JLabelIO()
        _ <- nameLabel.setText(INSERT_NAME)
        _ <- inputPanel.add(nameLabel)
        nameField <- JTextFieldIO()
        _ <- inputPanel.add(nameField)
        playersLabel <- JLabelIO()
        _ <- playersLabel.setText(INSERT_NUMBER)
        _ <- inputPanel.add(playersLabel)
        comboBoxPlayers <- JComboBoxIO()
        _ <- IO(VALUES.foreach(value => comboBoxPlayers.addItem(value).unsafeRunSync()))
        _ <- inputPanel.add(comboBoxPlayers)
        joinPublic <- JButtonIO(JOIN_PUBLIC)
        _ <- joinPublic.addActionListener(for {
          nameText <- nameField.text
          _ <- IO(if (checkName(nameField)) {
            guiRef.get ! PublicGameSubmitUi(nameText, comboBoxPlayers.selectedItem.unsafeRunSync())
          })
        } yield ())
        _ <- inputPanel.add(joinPublic)
        startPrivate <- JButtonIO(CREATE_PRIVATE)
        _ <- startPrivate.addActionListener(for {
          nameText <- nameField.text
          _ <- IO(if (checkName(nameField)) {
            guiRef.get ! CreatePrivateGameSubmitUi(nameText,comboBoxPlayers.selectedItem.unsafeRunSync())
          })
        } yield ())
        _ <- inputPanel.add(startPrivate)
        codeLabel <- JLabelIO()
        _ <- codeLabel.setText(INSERT_CODE)
        _ <- inputPanel.add(codeLabel)
        codeField <- JTextFieldIO()
        _ <- inputPanel.add(codeField)
        joinPrivate <- JButtonIO(JOIN_PRIVATE)
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
        _ <- menuPanel.background(Color.lightGray)
        _ <- inputPanel.background(Color.LIGHT_GRAY)
        _ <- cp.add(menuPanel)
        _ <- menuFrame.setResizable(false)
        _ <- menuFrame.setTitle(TITLE)
        _ <- menuFrame.setSize(WIDTH, HEIGHT)
        _  <- menuFrame.setLocationRelativeToInvokingAndWaiting(menuFrame.component)
        _ <- menuFrame.setVisible(true)
        _ <- menuFrame.addWindowListener(new WindowAdapter {
          override def windowClosing(e: WindowEvent): Unit = {
            guiRef.get ! PlayerCloseUi
          }
        })
        _ <- menuFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE)
      } yield ()

    private def checkName(nameField: JTextFieldIO): Boolean = nameField.text.unsafeRunSync() match {
      case "" => false
      case _ => true
    }

    private def checkCode(codeField: JTextFieldIO): Boolean = codeField.text.unsafeRunSync() match {
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