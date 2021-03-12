package it.amongsus.model.actor

import it.amongsus.controller.TimerStatus
import it.amongsus.core.map.DeadBody
import it.amongsus.core.player.Player
import it.amongsus.core.util.{ActionType, GameEnd, Direction}

object ModelActorMessages {
  /**
   * Tells to UI actor that the player is ready to start the game
   *
   * @param map of the game
   * @param players of the game
   */
  case class InitModel(map: Iterator[String], players: Seq[Player])
  /**
   * Tells to the model that his character moved
   *
   * @param direction to move on
   */
  case class MyCharMovedModel(direction: Direction)
  /**
   * Tells to the model that another player moved
   *
   * @param player of the game
   * @param deadBodys of the game
   */
  case class PlayerMovedModel(player: Player, deadBodys: Seq[DeadBody])
  /**
   * Tells to the model that a button is pressed
   *
   * @param action of the GUI
   */
  case class UiActionModel(action: ActionType)
  /**
   * Tells to the Ui actor that timer ended
   *
   * @param status of the timer
   */
  case class KillTimerStatusModel(status: TimerStatus)
  /**
   * Tells to the controller that the voting fase is beginning
   */
  case class BeginVotingModel()
  /**
   * Tells to the model that a player is killed
   *
   * @param username of the player
   */
  case class KillPlayerModel(username: String)
  /**
   * Tells to the model to restart the game
   */
  case class RestartGameModel()
  /**
   * Tells to the model that the game ended
   *
   * @param end of the game
   */
  case class GameEndModel(end: GameEnd)
  /**
   * Tells to the Ui Actor that a player left
   */
  case class MyPlayerLeftModel()
  /**
   * Tells to the Ui Actor that a player left
   *
   * @param clientId of the player
   */
  case class PlayerLeftModel(clientId: String)
}