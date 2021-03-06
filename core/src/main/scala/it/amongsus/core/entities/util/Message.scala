package it.amongsus.core.entities.util

/**
 * Trait that manages the messages of the Chat in the Game
 */
trait Message{
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

object Message {
  def apply(username: String, text: String) : Message = MessageImpl(username, text)

  private case class MessageImpl(override val username: String, override val text: String) extends Message
}
