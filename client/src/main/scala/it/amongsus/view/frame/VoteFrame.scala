package it.amongsus.view.frame

import java.awt.GridLayout

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.core.entities.player.Player
import it.amongsus.core.entities.util.Message
import it.amongsus.view.actor.UiActorGameMessages.{SendTextChatUi, SkipVoteUi, VoteUi}
import it.amongsus.view.frame.VoteFrame.VoteFrameImpl
import it.amongsus.view.swingio.{BorderFactoryIO, JButtonIO, JFrameIO, JLabelIO, JPanelIO, JScrollPaneIO, JTextAreaIO, JTextFieldIO}
import javax.swing.JFrame

/**
 * Trait of the Vote Frame that manages the voting of the players
 */
trait VoteFrame extends Frame {
  /**
   * Open the Vote Frame to allow players to vote the impostor
   *
   * @return
   */
  def start(): IO[Unit]
  /**
   * Display the eliminated player
   *
   * @param username of the player eliminated
   * @return
   */
  def eliminated(username: String): IO[Unit]
  /**
   * Save the state of the list of users of the game
   *
   * @return
   */
  def listUser: Seq[Player]
  /**
   * Method to append text in the chat
   *
   * @param text to append to the chat
   * @param username of the user that wrote a message in the chat
   * @return
   */
  def appendTextToChat(text: String, username: String): IO[Unit]
  /**
   * Method to append text in the Ghost Chat
   *
   * @param text to append to the chat
   * @param username of the user that wrote a message in the chat
   * @return
   */
  def appendTextToChatGhost(text: String, username: String): IO[Unit]
  /**
   * Method that opens the panel to wait the vote of other players to Ghost Players
   *
   * @return
   */
  def waitVote(): IO[Unit]
  /**
   * Method that notify no one players is eliminated during vote session
   *
   * @return
   */
  def noOneEliminated(): IO[Unit]
}

object VoteFrame {
  def apply(guiRef: Option[ActorRef], myPlayer: Player, listUser: Seq[Player]): VoteFrame =
    new VoteFrameImpl(guiRef: Option[ActorRef], myPlayer: Player, listUser: Seq[Player])

  /**
   * The Frame that manages the phase of voting
   *
   * @param guiRef ActorRef that is responsible to receiving and send all the messages about voting
   * @param myPlayer reference to player that should vote
   * @param listUser list of the user of the game
   */
  private class VoteFrameImpl(guiRef: Option[ActorRef], myPlayer: Player,
                              override val listUser: Seq[Player]) extends VoteFrame() {

    final val spaceDimension10: Int = 10
    final val spaceDimension20: Int = 10
    final val spaceDimension50: Int = 50
    final val spaceDimension60: Int = 60
    final val spaceDimension65: Int = -65
    final val spaceDimension180: Int = 200
    final val spaceDimension230: Int = 230
    final val gridRow4: Int = 4
    val frame = new JFrameIO(new JFrame("Among Sus - Voting"))
    val buttonVote: Array[JButtonIO] = new Array[JButtonIO](listUser.length)
    val votePanel: JPanelIO = JPanelIO().unsafeRunSync()
    val boxChat: JTextAreaIO = JTextAreaIO(spaceDimension20, spaceDimension20).unsafeRunSync()
    boxChat.focus()
    val scrollPane: JScrollPaneIO = JScrollPaneIO(boxChat).unsafeRunSync()
    val boxChatGhost: JTextAreaIO = JTextAreaIO(spaceDimension20, spaceDimension20).unsafeRunSync()
    boxChatGhost.focus()
    val scrollPaneGhost: JScrollPaneIO = JScrollPaneIO(boxChatGhost).unsafeRunSync()
    val WIDTH: Int = 1000
    val HEIGHT: Int = 800

    override def start(): IO[Unit] =
      for {
        menuBorder <- BorderFactoryIO.emptyBorderCreated(spaceDimension10,
          spaceDimension10, spaceDimension10, spaceDimension10)
        _ <- votePanel.setBorder(menuBorder)
        _ <- votePanel.setLayout(new GridLayout(1, 2))

        chooseVote <- JPanelIO()
        _ <- chooseVote.setLayout(new GridLayout(listUser.length + 2, 1))

        title <- JLabelIO()
        _ <- title.setText("Vote Player to Eliminate")
        borderTitle <- BorderFactoryIO.emptyBorderCreated(0, spaceDimension180, 0, 0)
        _ <- title.setBorder(borderTitle)
        _ <- chooseVote.add(title)

        buttonSkipVote <- JButtonIO("Skip Vote")

        _ <- IO(for (user <- listUser.indices) {
          buttonVote(user) = JButtonIO(listUser(user).username).unsafeRunSync()
          buttonVote(user).addActionListener(for {
            _ <- IO(guiRef.get ! VoteUi(listUser(user).username))
            _ <- IO(guiRef.get ! SendTextChatUi(Message(myPlayer.username, listUser(user).username)))
            _ <- boxChat.appendText(s"${myPlayer.username} vote ${listUser(user).username}\n")
            _ <- IO(buttonVote.foreach(p => p.setEnabled(false).unsafeRunSync()))
            _ <- buttonSkipVote.setEnabled(false)
          } yield()).unsafeRunSync()
          chooseVote.add(buttonVote(user)).unsafeRunSync()
        })

        _ <- buttonSkipVote.addActionListener(for {
          _ <- IO(guiRef.get ! SkipVoteUi())
          _ <- IO(guiRef.get ! SendTextChatUi(Message(myPlayer.username, "Skip Vote")))
          _ <- boxChat.appendText(s"${myPlayer.username} Skip Vote\n")
          _ <- IO(buttonVote.foreach(p => p.setEnabled(false).unsafeRunSync()))
          _ <- buttonSkipVote.setEnabled(false)
        } yield ())
        _ <- chooseVote.add(buttonSkipVote)

        _ <- votePanel.add(chooseVote)

        chatPanel <- JPanelIO()
        _ <- chatPanel.setLayout(new GridLayout(gridRow4, 1))
        titleChat <- JLabelIO()
        _ <- titleChat.setText("Chat")
        borderTitleChat <- BorderFactoryIO.emptyBorderCreated(spaceDimension65, spaceDimension230, 0, 0)
        _ <- titleChat.setBorder(borderTitleChat)
        _ <- chatPanel.add(titleChat)
        _ <- boxChat.appendText("Start Chatting!\n")
        _ <- chatPanel.add(scrollPane)
        chatField <- JTextFieldIO()
        _ <- chatPanel.add(chatField)
        sendText <- JButtonIO("Send Text")
        _ <- sendText.addActionListener(for {
          checkChatText <- chatField.text
          _ <- IO(if (checkText(chatField)) {
            boxChat.appendText(s"${myPlayer.username} said: $checkChatText\n").unsafeRunSync()
            guiRef.get ! SendTextChatUi(Message(myPlayer.username, checkChatText))
            chatField.clearText().unsafeRunSync()
          })
        } yield ())
        _ <- chatPanel.add(sendText)

        _ <- votePanel.add(chatPanel)

        cp <- frame.contentPane()
        _ <- cp.add(votePanel)
        _ <- frame.setResizable(false)
        _ <- frame.setTitle("Among Sus - Voting")
        _ <- frame.setSize(WIDTH, HEIGHT)
        _ <- frame.setVisible(true)
      } yield ()

    /**
     * Display the eliminated player
     *
     * @param username of the player eliminated
     * @return
     */
    override def eliminated(username: String): IO[Unit] = ???

    /**
     * Method to append text in the chat
     *
     * @param text     to append to the chat
     * @param username of the user that wrote a message in the chat
     * @return
     */
    override def appendTextToChat(text: String, username: String): IO[Unit] = ???

    /**
     * Method to append text in the Ghost Chat
     *
     * @param text     to append to the chat
     * @param username of the user that wrote a message in the chat
     * @return
     */
    override def appendTextToChatGhost(text: String, username: String): IO[Unit] = ???

    /**
     * Method that opens the panel to wait the vote of other players to Ghost Players
     *
     * @return
     */
    override def waitVote(): IO[Unit] = ???

    /**
     * Method that notify no one players is eliminated during vote session
     *
     * @return
     */
    override def noOneEliminated(): IO[Unit] = ???

    override def dispose(): IO[Unit] = for {
      _ <- frame.dispose()
    } yield ()

    private def checkText(nameField: JTextFieldIO): Boolean = nameField.text.unsafeRunSync() match {
      case "" => false
      case _ => true
    }

    override def appendTextToChat(text: String, username: String): IO[Unit] = for {
      _ <- boxChat.appendText(s"$username said: $text\n")
    } yield()
  }
}