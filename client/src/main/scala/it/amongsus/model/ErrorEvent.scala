package it.amongsus.model

sealed class ErrorEvent

object ErrorEvent {

  case class GenericError(reason: String) extends ErrorEvent

  case object ServerNotFound extends ErrorEvent

  case object LobbyCodeNotValid extends ErrorEvent

}