package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.core.entities.player.Player
import it.amongsus.view.frame.VoteFrame.VoteFrameImpl
import it.amongsus.view.swingio.{JButtonIO, JFrameIO, JPanelIO, JScrollPaneIO, JTextAreaIO}
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
   * @param guiRef   ActorRef that is responsible to receiving and send all the messages about voting
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

    override def start(): IO[Unit] = ???

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

    override def dispose(): IO[Unit] = ???
  }
}