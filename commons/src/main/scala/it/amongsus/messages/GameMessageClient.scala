package it.amongsus.messages

object GameMessageClient {
  /**
   * Tells the server that a player is ready to start a game
   */
  case class PlayerReadyClient()
  /**
   * Tells the server that a player wants to leave the game
   */
  case class LeaveGameClient()
  /**
   * Tells the gui actor that the game is won
   */
  case class GameWonClient()
  /**
   * Tells the gui actor that the game is lost
   */
  case class GameLostClient()
  /**
   * Tells the gui actor that the a player left
   */
  case class PlayerLeftClient()
  /**
   * Tells the gui actor that the his action is illegal
   */
  case class InvalidPlayerActionClient()
  /**
   * Tells the server that a client updated his state
   */
  case class GameStateUpdatedClient()
}
