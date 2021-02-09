package it.amongsus.view.actor

object UiActorGameMessages {
  /**
   * Tells the UI that a player is ready to start a game
   */
  case class PlayerReadyUi()
  /**
   * Tells the UI that a player wants to leave the game
   */
  case class LeaveGameUi()
  /**
   * Tells the UI actor that the game is won
   */
  case class GameWonUi()
  /**
   * Tells the UI actor that the game is lost
   */
  case class GameLostUi()
  /**
   * Tells the UI actor that the a player left
   */
  case class PlayerLeftUi()
  /**
   * Tells the UI actor that the his action is illegal
   */
  case class InvalidPlayerActionUi()
  /**
   * Tells the UI that a client updated his state
   */
  case class GameStateUpdatedUi()
}
