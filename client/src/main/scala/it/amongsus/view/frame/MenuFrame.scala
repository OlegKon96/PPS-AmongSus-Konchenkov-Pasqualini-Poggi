package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.view.swingio._
import java.awt.{BorderLayout, GridLayout}
import it.amongsus.view.actor.UiActorLobbyMessages._
import javax.swing.JFrame

/**
 *
 */
trait MenuFrame {
  /**
   *
   * @return
   */
  def start(): IO[Unit]

  /**
   *
   * @param numPlayers the number of the players
   * @return
   */
  def toLobby(numPlayers : Int): IO[Unit]

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

    val frame = new JFrameIO(new JFrame("Among Sus"))
    val lobbyView : LobbyFrame = LobbyFrame(this,guiRef.get)
    val WIDTH: Int = 500
    val HEIGHT: Int = 250
    var code : String = ""

    override def start(): IO[Unit] =
      for {
        menuPanel <- JPanelIO()
        menuBorder <- BorderFactoryIO.emptyBorderCreated(10, 10, 10, 10)
        _ <- menuPanel.setBorder(menuBorder)
        _ <- menuPanel.setLayout(new BorderLayout())
        titlePanel <- JPanelIO()
        titleBorder <- BorderFactoryIO.emptyBorderCreated(25, 200, 10, 200)
        _ <- titlePanel.setBorder(titleBorder)
        _ <- titlePanel.setLayout(new BorderLayout())
        title <- JLabelIO()
        _ <- title.setText("AMONG SUS")
        _ <- titlePanel.add(title,BorderLayout.CENTER)
        _ <- menuPanel.add(titlePanel,BorderLayout.NORTH)
        inputPanel <- JPanelIO()
        _ <- inputPanel.setLayout(new GridLayout(5,1))
        nameField <- JTextFieldIO()
        _ <- inputPanel.add(nameField)
        joinPublic <- JButtonIO("Partecipa ad una partita pubblica")
        _ <- joinPublic.addActionListener(for {
          nameText <- nameField.text
          _ <- IO(if(checkName(nameField)) {
            guiRef.get ! PublicGameSubmitUi(nameText,3)
          })
        } yield())
        _ <- inputPanel.add(joinPublic)
        startPrivate <- JButtonIO("Crea una partita privata")
        _ <- startPrivate.addActionListener(for {
          nameText <- nameField.text
          _ <- IO(if(checkName(nameField)) {
            guiRef.get ! CreatePrivateGameSubmitUi(nameText,3)
          })
        } yield())
        _ <- inputPanel.add(startPrivate)
        codeField <- JTextFieldIO()
        _ <- inputPanel.add(codeField)
        joinPrivate <- JButtonIO("Partecipa ad una partita privata")
        _ <- joinPrivate.addActionListener(for {
          codeText <- codeField.text
          nameText <- nameField.text
          _ <- IO(if(checkName(nameField) && checkCode(codeField)){
            code = codeText
            guiRef.get ! PrivateGameSubmitUi(nameText,code)
          })
        } yield())
        _ <- inputPanel.add(joinPrivate)
        _ <- menuPanel.add(inputPanel,BorderLayout.SOUTH)
        cp <- frame.contentPane()
        _ <- cp.add(menuPanel)
        _ <- frame.setResizable(false)
        _ <- frame.setSize(WIDTH, HEIGHT)
        _ <- frame.setVisible(true)
        _ <- IO(guiRef.get ! InitFrame(this,lobbyView))
      } yield ()

    private def checkName(nameField: JTextFieldIO): Boolean = nameField.text.unsafeRunSync() match {
      case "" => false
      case _ => true
    }

    private def checkCode(codeField : JTextFieldIO) : Boolean =  codeField.text.unsafeRunSync() match {
      case "" => false
      case _ => true
    }

    override def toLobby(numPlayers: Int): IO[Unit] = for {
      _ <- frame.dispose()
      _ <- lobbyView start(numPlayers,code)
    } yield()

    override def saveCode(lobbyCode: String): Unit = {
      code = lobbyCode
    }

    override def lobbyError(): Unit = {
      code = ""
    }
  }
}