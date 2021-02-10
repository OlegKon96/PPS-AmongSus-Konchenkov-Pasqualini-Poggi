package it.amongsus.messages

object GameMessageClient {
  /**
   * Tells the controller actor that a player is ready to start a game
   */
  case class PlayerReadyClient()
  /**
   * Tells the controller actor that a player wants to leave the game
   */
  case class LeaveGameClient()
  /**
   * Tells the controller actor that the game is ended with a win
   */
  case class GameWonClient()
  /**
   * Tells the controller actor that the game is ended with a lose
   */
  case class GameLostClient()
  /**
   * Tells the controller actor that the player left the game
   */
  case class PlayerLeftClient()
  /**
   * Tells the controller actor that player actions is illegal
   */
  case class InvalidPlayerActionClient()
  /**
   * Tells the controller actor that the Client updated his state
   */
  case class GameStateUpdatedClient()
}