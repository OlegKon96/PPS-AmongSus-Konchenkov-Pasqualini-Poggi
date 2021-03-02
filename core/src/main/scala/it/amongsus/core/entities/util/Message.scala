package it.amongsus.core.entities.util

/**
 * Trait that manages the messages of the Chat in the Game
 */
trait Message{
  def username: String
  def text: String
}

object Message {
  def apply(username: String, text: String) : Message = MessageImpl(username, text)

  private case class MessageImpl(override val username: String, override val text: String) extends Message
}
