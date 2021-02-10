package it.amongsus.model

sealed class ErrorEvent

object ErrorEvent {

  /**
   * Generic Error Message that was Occurred
   *
   * @param reason the string that describes the error
   */
  case class GenericError(reason: String) extends ErrorEvent
  /**
   * Error Message that the Server is not Found
   */
  case object ServerNotFound extends ErrorEvent
  /**
   * Error Message that the Lobby Code inserted is not valid
   */
  case object LobbyCodeNotValid extends ErrorEvent
}