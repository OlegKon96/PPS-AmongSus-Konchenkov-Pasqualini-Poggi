package it.amongsus.view.frame

import akka.actor.ActorRef
import cats.effect.IO
import it.amongsus.core.entities.player.Player
import it.amongsus.view.frame.VoteFrame.VoteFrameImpl

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
    /**
     * Open the Vote Frame to allow players to vote the impostor
     *
     * @return
     */
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