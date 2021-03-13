package it.amongsus.core.util

/**
 * Trait that manages the messages of the Chat in the Game
 */
trait ChatMessage{
  /**
   * Username of the player that send the message
   *
   * @return
   */
  def username: String
  /**
   * Text of the message
   *
   * @return
   */
  def text: String
}

object ChatMessage {
  def apply(username: String, text: String) : ChatMessage = ChatMessageImpl(username, text)

  private case class ChatMessageImpl(override val username: String, override val text: String) extends ChatMessage
}