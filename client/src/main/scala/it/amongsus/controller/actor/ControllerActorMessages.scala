package it.amongsus.controller.actor

import akka.actor.ActorRef
import it.amongsus.controller.TimerStatus
import it.amongsus.core.map.{Coin, DeadBody}
import it.amongsus.core.player.Player
import it.amongsus.core.util.MapHelper.GameMap
import it.amongsus.core.util.{ActionType, ChatMessage, Direction, GameEnd}

object ControllerActorMessages {
  /**
   * Tells to the controller that the model is ready.
   */
  case class ModelReadyController(map: GameMap, myChar: Player, players: Seq[Player],
                                  coins: Seq[Coin])
  /**
   * Tells to the controller that his character is moved.
   */
  case class MyCharMovedController(direction: Direction)
  /**
   * Tells to the controller that the model has updated his character status.
   */
  case class UpdatedMyCharController(player: Player, gamePlayers: Seq[Player], deadBodys: Seq[DeadBody])
  /**
   * Tells to the controller that the model has updated a player status.
   */
  case class UpdatedPlayersController(myChar: Player, player: Seq[Player], coins: Seq[Coin],
                                      deadBodies: Seq[DeadBody])
  /**
   * Tells to the controller that the Ui Button is pressed.
   *
   * @param action that is pressed.
   */
  case class UiActionController(action: ActionType)
  /**
   * Tells to the controller that the button can be clicked.
   *
   * @param action that can be clicked.
   */
  case class ActionOnController(action: ActionType)
  /**
   * Tells to the controller that the button can't be clicked.
   *
   * @param action that can be clicked.
   */
  case class ActionOffController(action: ActionType)
  /**
   * Tells to the controller that it's time to vote.
   */
  case object InitVote
  /**
   * Tells to the controller that the game is ended.
   */
  case class GameEndController(end: GameEnd)
  /**
   * Tells to the controller that the game needs to be restarted.
   */
  case object RestartGameController
  /**
   * Tells to the controller that the voting fase is beginning.
   */
  case class BeginVotingController(players: Seq[Player])
  /**
   * Tells to the controller the timing of the action of players.
   *
   * @param status of the timer.
   */
  case class KillTimerController(status: TimerStatus)
  /**
   * Tells to the Ui Actor to send a text messages.
   *
   * @param message to send to the chat.
   * @param char that sent the message.
   */
  case class SendTextChatController(message: ChatMessage, char: Player)
  /**
   * Tells to the Ui Actor that Players left the game.
   */
  case object PlayerLeftController
  /**
   * Tells to the Ui Actor to change the behaviour.
   *
   * @param model references to the model actor.
   * @param server references to the server actor.
   */
  case class TestGameBehaviour(model: ActorRef, server: ActorRef)
}