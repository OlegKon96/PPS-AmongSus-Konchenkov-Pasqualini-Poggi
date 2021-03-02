package it.amongsus.controller.actor

import it.amongsus.controller.TimerStatus
import it.amongsus.core.entities.map.{Collectionable, DeadBody, Tile}
import it.amongsus.core.entities.player.Player
import it.amongsus.core.entities.util.{ButtonType, GameEnd, Movement}

object ControllerActorMessages {
  /**
   * Tells to the controller that the model is ready
   */
  case class ModelReadyCotroller(map: Array[Array[Tile]], myChar: Player, players: Seq[Player],
                                 collectionables: Seq[Collectionable])
  /**
   * Tells to the controller that his character is moved
   */
  case class MyCharMovedCotroller(direction: Movement)
  /**
   * Tells to the controller that the model has updated his character status
   */
  case class UpdatedMyCharController(player: Player, gamePlayers: Seq[Player], deadBodys: Seq[DeadBody])
  /**
   * Tells to the controller that the model has updated a player status
   */
  case class UpdatedPlayersController(myChar: Player, player: Seq[Player], collectionables: Seq[Collectionable],
                                      deadBodies: Seq[DeadBody])
  /**
   * Tells to the controller that the Ui Button is pressed
   * @param button that is pressed
   */
  case class UiButtonPressedController(button: ButtonType)
  /**
   * Tells to the controller that the button can be clicked
   * @param button that can be clicked
   */
  case class ButtonOnController(button: ButtonType)
  /**
   * Tells to the controller that the button can't be clicked
   * @param button that can be clicked
   */
  case class ButtonOffController(button: ButtonType)
  /**
   * Tells to the controller that it's time to vote
   */
  case class InitVote()
  /**
   * Tells to the controller that the game is ended
   */
  case class GameEndController(end: GameEnd)
  /**
   * Tells to the controller that the game needs to be restarted
   */
  case class RestartGameController()
  /**
   * Tells to the controller that the voting fase is beginning
   */
  case class BeginVotingController(players: Seq[Player])
  /**
   * Tells to the controller the timing of the action of players
   *
   * @param status of the timer
   */
  case class KillTimerController(status: TimerStatus)
}
