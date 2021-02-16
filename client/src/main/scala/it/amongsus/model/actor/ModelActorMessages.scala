package it.amongsus.model.actor

import it.amongsus.core.entities.player.{Movement, Player}

object ModelActorMessages {
  /**
   * Tells to UI actor that the player is ready to start the game
   */
  case class InitMapModel(map: Iterator[String])

  /**
   * Tells to UI actor that the player is ready to start the game
   */
  case class InitPlayersModel(players: Seq[Player])
  /**
   * Tells to the model that his character moved
   */
  case class MyCharMovedModel(direction: Movement)
  /**
   * Tells to the model that another player moved
   */
  case class PlayerMovedModel(player: Player)
}
