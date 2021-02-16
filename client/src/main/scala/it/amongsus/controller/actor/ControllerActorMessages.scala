package it.amongsus.controller.actor

import it.amongsus.core.entities.map.{Collectionable, Tile}
import it.amongsus.core.entities.player.{Movement, Player}

object ControllerActorMessages {
  /**
   * Tells to the controller that the model is ready
   */
  case class ModelReadyCotroller(map: Array[Array[Tile]], players: Seq[Player], collectionables: Seq[Collectionable])
  /**
   * Tells to the controller that his character moved
   */
  case class MyCharMovedCotroller(direction: Movement)
  /**
   * Tells to the controller that the model has updated his character status
   */
  case class UpdatedMyCharController(player: Player)
  /**
   * Tells to the controller that the model has updated a player status
   */
  case class UpdatedPlayerController(player: Player)
}
