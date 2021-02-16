package it.amongsus.view.actor

import it.amongsus.core.entities.player.{Movement, Player}

object UiActorGameMessages {
  /**
   * Tells to UI actor that the player is ready to start the game
   */
  case class PlayerReadyUi()
  /**
   * Tells to UI actor that the player wants to leave the game
   */
  case class LeaveGameUi()
  /**
   * Tells to UI actor that the game is ended with a win
   */
  case class GameWonUi()
  /**
   * Tells to UI actor that the game is ended with a lose
   */
  case class GameLostUi()
  /**
   * Tells to the UI actor that his character moved
   */
  case class MyCharMovedUi(direction: Movement)
  /**
   * Tells to the ui actor that a player was updated
   */
  case class PlayerUpdatedUi(player: Player)
  /**
   * Tells to UI actor that the player has left the game
   */
  case class PlayerLeftUi()
  /**
   * Tells the UI actor that his action is illegal in the game
   */
  case class InvalidPlayerActionUi()
  /**
   * Tells the UI actor that the Client has updated his state
   */
  case class GameStateUpdatedUi()
}