package it.amongsus.view.actor

import it.amongsus.core.entities.map.{Collectionable, DeadBody}
import it.amongsus.core.entities.player.Player
import it.amongsus.core.entities.util.{ButtonType, GameEnd, Message, Movement}

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
  case class GameEndUi(end: GameEnd)
  /**
   * Tells to UI actor that a Button is pressed
   */
  case class UiButtonPressedUi(button: ButtonType)
  /**
   * Tells to the controller that his character moved
   */
  case class MyCharMovedUi(direction: Movement)
  /**
   * Tells to the ui actor that a player was updated
   */
  case class PlayerUpdatedUi(myChar: Player, player: Seq[Player],
                             collectionables: Seq[Collectionable], deadBodies: Seq[DeadBody])
  /**
   * Tells to UI actor that a Button can be pressed
   */
  case class ButtonOnUi(button: ButtonType)
  /**
   * Tells to UI actor that a Button can't be pressed
   */
  case class ButtonOffUi(button: ButtonType)
  /**
   * Tells to UI actor that the player has left the game
   */
  case class PlayerLeftUi(clientId: String)
  /**
   * Tells to UI actor that a player wants to vote another player
   */
  case class VoteUi(username: String)
  /**
   * Tells to UI actor to restart the game
   */
  case class RestartGameUi()
  /**
   * Tells to the Ui actor that the voting fase is beginning
   */
  case class BeginVotingUi(players: Seq[Player])
  /**
   * Tells to the Ui actor that timer updated
   */
  case class KillTimerUpdateUi(minutes: Long, seconds: Long)
  /**
   * Tells to the Ui actor that timer updated
   */
  case class SabotageTimerUpdateUi(minutes: Long, seconds: Long)
  /**
   * Tells to the Ui Actor to send a text messages
   */
  case class SendTextChatUi(message: Message, char: Player)
  /**
   * Tells to the Ui Actor to send a text messages
   */
  case class ReceiveTextChatUi(message: Message)
  /**
   * Tells to the Ui Actor to send a text messages in the Ghost Chat
   */
  case class ReceiveTextChatGhostUi(message: Message)
  /**
   * Tells to the Ui Actor that no one was ejected from vote session
   */
  case class NoOneEliminatedUi()
}