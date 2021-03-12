package it.amongsus.view.actor

import it.amongsus.core.map.{Collectionable, DeadBody}
import it.amongsus.core.player.Player
import it.amongsus.core.util.{ButtonType, GameEnd, ChatMessage, Movement}

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
   *
   * @param end of the game
   */
  case class GameEndUi(end: GameEnd)
  /**
   * Tells to UI actor that a Button is pressed
   *
   * @param button that is pressed
   */
  case class UiButtonPressedUi(button: ButtonType)
  /**
   * Tells to the controller that his character moved
   *
   * @param direction to move the player
   */
  case class MyCharMovedUi(direction: Movement)
  /**
   * Tells to the ui actor that a player was updated
   *
   * @param myChar in the game
   * @param player of the game
   * @param collectionables of the game
   * @param deadBodies of the game
   */
  case class PlayerUpdatedUi(myChar: Player, player: Seq[Player],
                             collectionables: Seq[Collectionable], deadBodies: Seq[DeadBody])
  /**
   * Tells to UI actor that a Button can be pressed
   *
   * @param button that is pressed
   */
  case class ButtonOnUi(button: ButtonType)
  /**
   * Tells to UI actor that a Button can't be pressed
   *
   * @param button that can't be pressed
   */
  case class ButtonOffUi(button: ButtonType)
  /**
   * Tells to UI actor that the player has left the game
   *
   * @param clientId of the player
   */
  case class PlayerLeftUi(clientId: String)
  /**
   * Tells to UI actor that a player wants to vote another player
   *
   * @param username that is voted
   */
  case class VoteUi(username: String)
  /**
   * Tells to UI actor to restart the game
   */
  case class RestartGameUi()
  /**
   * Tells to the Ui actor that the voting phase is beginning
   *
   * @param players of the game
   */
  case class BeginVotingUi(players: Seq[Player])
  /**
   * Tells to the Ui actor that timer updated
   *
   * @param minutes of the timer
   * @param seconds of the timer
   */
  case class KillTimerUpdateUi(minutes: Long, seconds: Long)
  /**
   * Tells to the Ui actor that timer updated
   *
   * @param minutes of the timer
   * @param seconds of the timer
   */
  case class SabotageTimerUpdateUi(minutes: Long, seconds: Long)
  /**
   * Tells to the Ui Actor to send a text messages
   *
   * @param message that is sent
   * @param char that sends the message
   */
  case class SendTextChatUi(message: ChatMessage, char: Player)
  /**
   * Tells to the Ui Actor to send a text messages
   *
   * @param message that is received
   */
  case class ReceiveTextChatUi(message: ChatMessage)
  /**
   * Tells to the Ui Actor that no one was ejected from vote session
   */
  case class NoOneEliminatedUi()
}