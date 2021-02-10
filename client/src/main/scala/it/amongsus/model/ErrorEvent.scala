package it.amongsus.model

sealed class ErrorEvent

object ErrorEvent {

  /**
   * A Generic Error Occurred
   * @param reason the string that describe the error
   */
  case class GenericError(reason: String) extends ErrorEvent

  /**
   * Message of Error that Server Not Found
   */
  case object ServerNotFound extends ErrorEvent

  /**
   * Message that the Lobby Code inserted is not valid
   */
  case object LobbyCodeNotValid extends ErrorEvent
}