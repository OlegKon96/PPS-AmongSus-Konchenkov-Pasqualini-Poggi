package it.amongsus.controller.actor

sealed class GameStartupEvent

case class LobbyJoinErrorEvent(error: ErrorEvent) extends GameStartupEvent