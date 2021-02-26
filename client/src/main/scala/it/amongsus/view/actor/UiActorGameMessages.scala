package it.amongsus.view.actor

import it.amongsus.core.entities.map.{Collectionable, DeadBody}
import it.amongsus.core.entities.player.Player
import it.amongsus.core.entities.util.{ButtonType, Movement}

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
  case class PlayerUpdatedUi(myChar: Player, player: Seq[Player], collectionables: Seq[Collectionable],
                             deadBodies: Seq[DeadBody])
  /**
   * Tells to UI actor that a Button can be pressed
   */
  case class ButtonOnUi(button: ButtonType)
  /**
   * Tells to UI actor that a Button can't be pressed
   */
  case class ButtonOffUi(button: ButtonType)
  /**
   * Tells to UI actor that a Button is pressed
   */
  case class UiButtonPressedUi(button: ButtonType)
  /**
   * Tells to UI actor that the player has left the game
   */
  case class PlayerLeftUi()
  /**
   * Tells to the Ui actor that timer updated
   */
  case class KillTimerUpdateUi(minutes: Long, seconds: Long)
}