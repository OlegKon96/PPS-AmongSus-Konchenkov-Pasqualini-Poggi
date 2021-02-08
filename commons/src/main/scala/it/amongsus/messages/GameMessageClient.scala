package it.amongsus.messages

object GameMessageClient {
  /**
   * Tells the server that a player is ready to start a game
   */
  case class PlayerReady()
  /**
   * Tells the server that a player wants to leave the game
   */
  case class LeaveGame()
  /**
   * Tells the gui actor that the game is won
   */
  case class GameWon()
  /**
   * Tells the gui actor that the game is lost
   */
  case class GameLost()
  /**
   * Tells the gui actor that the a player left
   */
  case class GameEndedBecousePlayerLeft()
  /**
   * Tells the gui actor that the his action is illegal
   */
  case class InvalidPlayerAction()
  /**
   * Tells the server that a client updated his state
   */
  case class GameStateUpdated()
}
