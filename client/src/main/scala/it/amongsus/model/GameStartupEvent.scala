package it.amongsus.model

sealed class GameStartupEvent

case object LobbyJoinedEvent extends GameStartupEvent

case class PrivateLobbyCreatedEvent(privateCode: String) extends GameStartupEvent
case class GameStartedEvent() extends GameStartupEvent
case class LobbyJoinErrorEvent(error: ErrorEvent) extends GameStartupEvent