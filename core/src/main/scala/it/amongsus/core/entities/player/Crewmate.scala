package it.amongsus.core.entities.player

import it.amongsus.core.entities.map.Collectionable

/**
 * Trait that manages Crewmate
 */
trait Crewmate extends Player {
  /**
   * Number of the coin
   */
  val numCoins: Int

  /**
   * Method to collect the coin
   *
   * @param player of the game
   * @return
   */
  def collect(player: Crewmate):  Player = {
    player match {
      case alive: CrewmateAlive => CrewmateAlive(alive.clientId, alive.username, alive.numCoins + 1, alive.position)
      case ghost: CrewmateGhost => CrewmateGhost(ghost.clientId, ghost.username, ghost.numCoins + 1, ghost.position)
    }
  }

  def canCollect(collectionables: Seq[Collectionable], player: Crewmate): Option[Collectionable] = {
    collectionables.find(coin => coin.position == player.position)
  }
}