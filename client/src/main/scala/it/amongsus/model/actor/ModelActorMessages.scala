package it.amongsus.model.actor

import it.amongsus.controller.TimerStatus
import it.amongsus.core.entities.map.DeadBody
import it.amongsus.core.entities.player.Player
import it.amongsus.core.entities.util.{ButtonType, GameEnd, Movement}

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
  case class MyCharMovedModel(direction: Movement)
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
   * @param button of the GUI
   */
  case class UiButtonPressedModel(button: ButtonType)
  /**
   * Tells to the Ui actor that timer ended
   */
  case class KillTimerStatusModel(status: TimerStatus)
  /**
   * Tells to the controller that the voting fase is beginning
   */
  case class BeginVotingModel()
  /**
   * Tells to the model to restart the game
   */
  case class RestartGameModel()
  /**
   * Tells to the model that the game ended
   */
  case class GameEndModel(end: GameEnd)
}
