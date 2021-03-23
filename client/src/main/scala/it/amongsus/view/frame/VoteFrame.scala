package it.amongsus.view.frame

import java.awt.event.{WindowAdapter, WindowEvent}
import java.awt.{BorderLayout, GridLayout}
import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.core.player.{Crewmate, Impostor, Player}
import it.amongsus.core.util.ChatMessage
import it.amongsus.view.actor.UiActorGameMessages.{SendTextChatUi, VoteUi}
import it.amongsus.view.frame.Constants.VoteFrame.Numbers._
import it.amongsus.view.frame.Constants.VoteFrame.Strings._
import it.amongsus.view.swingio.{BorderFactoryIO, JButtonIO, JFrameIO, JLabelIO, JPanelIO, JScrollPaneIO, JTextAreaIO}
import it.amongsus.view.swingio.JTextFieldIO
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

    val frame = new JFrameIO(new JFrame(TITLE_MAIN_FRAME))
    val buttonVote: Array[JButtonIO] = new Array[JButtonIO](listUser.length)
    val votePanel: JPanelIO = JPanelIO().unsafeRunSync()
    val boxChat: JTextAreaIO = JTextAreaIO(SPACE_DIMENSION_20, SPACE_DIMENSION_20).unsafeRunSync()
    boxChat.focus()
    boxChat.setEditable()
    val scrollPane: JScrollPaneIO = JScrollPaneIO(boxChat).unsafeRunSync()
    val boxChatGhost: JTextAreaIO = JTextAreaIO(SPACE_DIMENSION_20, SPACE_DIMENSION_20).unsafeRunSync()
    boxChatGhost.focus()
    boxChatGhost.setEditable()
    val scrollPaneGhost: JScrollPaneIO = JScrollPaneIO(boxChatGhost).unsafeRunSync()

    override def start(): IO[Unit] =
      for {
        menuBorder <- BorderFactoryIO.emptyBorderCreated(SPACE_DIMENSION_10,
          SPACE_DIMENSION_10, SPACE_DIMENSION_10, SPACE_DIMENSION_10)
        _ <- votePanel.setBorder(menuBorder)
        _ <- votePanel.setLayout(new GridLayout(1, 2))

        chooseVote <- JPanelIO()
        _ <- chooseVote.setLayout(new GridLayout(listUser.length + 2, 1))

        title <- JLabelIO()
        _ <- title.setText(TITLE_FRAME_VOTE_PLAYER)
        borderTitle <- BorderFactoryIO.emptyBorderCreated(0, SPACE_DIMENSION_180, 0, 0)
        _ <- title.setBorder(borderTitle)
        _ <- chooseVote.add(title)

        buttonSkipVote <- JButtonIO(SKIP_VOTE)

        _ <- IO(for (user <- listUser.indices) {
          buttonVote(user) = JButtonIO(listUser(user).username).unsafeRunSync()
          buttonVote(user).addActionListener(for {
            _ <- IO(guiRef.get ! VoteUi(listUser(user).username))
            _ <- IO(guiRef.get ! SendTextChatUi(ChatMessage(myPlayer.username, listUser(user).username),
              listUser.find(p => p.username == myPlayer.username).get))
            _ <- boxChat.appendText(s"${myPlayer.username} vote ${listUser(user).username}\n")
            _ <- IO(buttonVote.foreach(p => p.setEnabled(false).unsafeRunSync()))
            _ <- buttonSkipVote.setEnabled(false)
          } yield()).unsafeRunSync()
          chooseVote.add(buttonVote(user)).unsafeRunSync()
        })

        _ <- buttonSkipVote.addActionListener(for {
          _ <- IO(guiRef.get ! VoteUi(""))
          _ <- IO(guiRef.get ! SendTextChatUi(ChatMessage(myPlayer.username, SKIP_VOTE),
            listUser.find(p => p.username == myPlayer.username).get))
          _ <- boxChat.appendText(s"${myPlayer.username} Skip Vote\n")
          _ <- IO(buttonVote.foreach(p => p.setEnabled(false).unsafeRunSync()))
          _ <- buttonSkipVote.setEnabled(false)
        } yield ())
        _ <- chooseVote.add(buttonSkipVote)

        _ <- votePanel.add(chooseVote)

        chatPanel <- JPanelIO()
        _ <- chatPanel.setLayout(new GridLayout(GRID_ROW_4, 1))
        titleChat <- JLabelIO()
        _ <- titleChat.setText(CHAT)
        borderTitleChat <- BorderFactoryIO.emptyBorderCreated(SPACE_DIMENSION_65, SPACE_DIMENSION_230, 0, 0)
        _ <- titleChat.setBorder(borderTitleChat)
        _ <- chatPanel.add(titleChat)
        _ <- boxChat.appendText(START_CHATTING)
        _ <- chatPanel.add(scrollPane)
        chatField <- JTextFieldIO()
        _ <- chatPanel.add(chatField)
        sendText <- JButtonIO(SEND_TEXT)
        _ <- sendText.addActionListener(for {
          checkChatText <- chatField.text
          _ <- IO(if (checkText(chatField)) {
            boxChat.appendText(s"${myPlayer.username} said: $checkChatText\n").unsafeRunSync()
            guiRef.get ! SendTextChatUi(ChatMessage(myPlayer.username, checkChatText),
              listUser.find(p => p.username == myPlayer.username).get)
            chatField.clearText().unsafeRunSync()
          })
        } yield ())
        _ <- chatPanel.add(sendText)

        _ <- votePanel.add(chatPanel)

        cp <- frame.contentPane()
        _ <- cp.add(votePanel)
        _ <- frame.setResizable(false)
        _ <- frame.setTitle(TITLE_MAIN_FRAME)
        _ <- frame.setSize(WIDTH, HEIGHT)
        _ <- frame.setVisible(true)
        _ <- frame.addWindowListener(new WindowAdapter {
          override def windowClosing(e: WindowEvent): Unit = {
            guiRef.get ! VoteUi("")
          }
        })
      } yield ()

    override def dispose(): IO[Unit] = for {
      _ <- frame.dispose()
    } yield ()

    override def eliminated(username: String): IO[Unit] = {
      val role = listUser.find(p => p.username == username).get match {
        case _: Crewmate => CREWMATE
        case _: Impostor => IMPOSTOR
      }
      for {
        cp <- frame.contentPane()
        _ <- cp.remove(votePanel)
        eliminationPanel <- JPanelIO()
        _ <- eliminationPanel.setLayout(new BorderLayout())
        text <- JLabelIO()
        borderText <- BorderFactoryIO.emptyBorderCreated(SPACE_DIMENSION_60, SPACE_DIMENSION_60, 0, 0)
        _ <- text.setBorder(borderText)
        _ <- text.setText(s"The Eliminated Player is: $username and is an: $role")
        _ <- eliminationPanel.add(text, BorderLayout.NORTH)
        _ <- cp.add(eliminationPanel)
        _ <- frame.setResizable(false)
        _ <- frame.setTitle(TITLE_FRAME_ELIMINATED_PLAYER)
        _ <- frame.setSize(WIDTH/2, HEIGHT/4)
        _ <- frame.setVisible(true)
      } yield ()
    }

    private def checkText(nameField: JTextFieldIO): Boolean = nameField.text.unsafeRunSync() match {
      case "" => false
      case _ => true
    }

    override def appendTextToChat(text: String, username: String): IO[Unit] = for {
      _ <- boxChat.appendText(s"$username said: $text\n")
    } yield()

    override def waitVote(): IO[Unit] = for {
      menuBorder <- BorderFactoryIO.emptyBorderCreated(SPACE_DIMENSION_10,
        SPACE_DIMENSION_10, SPACE_DIMENSION_10, SPACE_DIMENSION_10)
      _ <- votePanel.setBorder(menuBorder)
      _ <- votePanel.setLayout(new GridLayout(1, 2))

      chooseVote <- JPanelIO()
      _ <- chooseVote.setLayout(new GridLayout(1, 1))

      waitGhostPanel <- JPanelIO()
      _ <- waitGhostPanel.setLayout(new BorderLayout())
      text <- JLabelIO()
      borderText <- BorderFactoryIO.emptyBorderCreated(SPACE_DIMENSION_60, SPACE_DIMENSION_60, 0, 0)
      _ <- text.setBorder(borderText)
      _ <- text.setText(WAIT_VOTE_OTHER)
      _ <- waitGhostPanel.add(text, BorderLayout.NORTH)
      _ <- chooseVote.add(waitGhostPanel)

      _ <- votePanel.add(chooseVote)

      chatPanel <- JPanelIO()
      _ <- chatPanel.setLayout(new GridLayout(GRID_ROW_4, 1))
      titleChat <- JLabelIO()
      _ <- titleChat.setText(CHAT)
      borderTitleChat <- BorderFactoryIO.emptyBorderCreated(0, SPACE_DIMENSION_230, 0, 0)
      _ <- titleChat.setBorder(borderTitleChat)
      _ <- chatPanel.add(titleChat)
      _ <- boxChatGhost.appendText(START_CHATTING)
      _ <- chatPanel.add(scrollPaneGhost)
      chatField <- JTextFieldIO()
      _ <- chatPanel.add(chatField)
      sendText <- JButtonIO(SEND_TEXT)
      _ <- sendText.addActionListener(for {
        checkChatText <- chatField.text
        _ <- IO(if (checkText(chatField)) {
          boxChatGhost.appendText(s"${myPlayer.username} said: $checkChatText\n").unsafeRunSync()
          guiRef.get ! SendTextChatUi(ChatMessage(myPlayer.username, checkChatText),
            listUser.find(p => p.username == myPlayer.username).get)
          chatField.clearText().unsafeRunSync()
        })
      } yield ())
      _ <- chatPanel.add(sendText)

      _ <- votePanel.add(chatPanel)

      cp <- frame.contentPane()
      _ <- cp.add(votePanel)
      _ <- frame.setResizable(false)
      _ <- frame.setTitle(TITLE_MAIN_FRAME)
      _ <- frame.setSize(WIDTH, HEIGHT)
      _ <- frame.setVisible(true)
    } yield ()

    override def appendTextToChatGhost(text: String, username: String): IO[Unit] = for {
      _ <- boxChatGhost.appendText(s"$username said: $text\n")
    } yield()

    override def noOneEliminated(): IO[Unit] = {
      for {
        cp <- frame.contentPane()
        _ <- cp.remove(votePanel)
        waitGhostPanel <- JPanelIO()
        _ <- waitGhostPanel.setLayout(new BorderLayout())
        text <- JLabelIO()
        borderText <- BorderFactoryIO.emptyBorderCreated(SPACE_DIMENSION_50, SPACE_DIMENSION_50, 0, 0)
        _ <- text.setBorder(borderText)
        _ <- text.setText(NO_ONE_EJECTED)
        _ <- waitGhostPanel.add(text, BorderLayout.NORTH)
        _ <- cp.add(waitGhostPanel)
        _ <- frame.setResizable(false)
        _ <- frame.setTitle(TITLE_FRAME_EXIT_POOL)
        _ <- frame.setSize(WIDTH/3, HEIGHT/4)
        _ <- frame.setVisible(true)
      } yield ()
    }
  }
}