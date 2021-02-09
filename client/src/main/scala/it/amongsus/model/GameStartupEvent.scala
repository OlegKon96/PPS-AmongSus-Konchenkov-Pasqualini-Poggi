package it.amongsus.model

sealed class GameStartupEvent

case class LobbyJoinErrorEvent(error: ErrorEvent) extends GameStartupEvent