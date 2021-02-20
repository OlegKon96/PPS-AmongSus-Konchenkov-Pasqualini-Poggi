package it.amongsus.model.actor

import it.amongsus.core.entities.map.DeadBody
import it.amongsus.core.entities.player.Player
import it.amongsus.core.entities.util.Movement

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
   */
  case class MyCharMovedModel(direction: Movement)
  /**
   * Tells to the model that another player moved
   *
   * @param player of the game
   * @param deadBodys of the game
   */
  case class PlayerMovedModel(player: Player, deadBodys: Seq[DeadBody])
}
