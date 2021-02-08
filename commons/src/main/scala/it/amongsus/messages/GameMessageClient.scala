package it.amongsus.messages

object GameMessageClient {

  case class PlayerReady()

  case class LeaveGame()

  case class GameWon()

  case class GameLost()

  case class GameEndedBecousePlayerLeft()

  case class InvalidPlayerAction()

  case class GameStateUpdated()
}
