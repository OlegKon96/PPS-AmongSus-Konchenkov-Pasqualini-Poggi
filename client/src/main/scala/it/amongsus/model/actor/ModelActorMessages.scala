package it.amongsus.model.actor

import it.amongsus.core.entities.player.Player
import it.amongsus.core.entities.util.Movement

object ModelActorMessages {
  /**
   * Tells to the model actor to initialize the map
   */
  case class InitMapModel(map: Iterator[String])

  /**
   * Tells to the model actor to initialize the players
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
