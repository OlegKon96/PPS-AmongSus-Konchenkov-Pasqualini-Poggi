package it.amongsus.server.lobby

import it.amongsus.server.common.Player

object Lobby {
  def apply(numberOfPlayers: Int): Lobby[Player] = GameLobby[Player](numberOfPlayers)
}

/**
 * Trait that manages the Lobby of the game
 *
 * @tparam T generic player abstraction
 */
trait Lobby[T <: Player] {
  /**
   * List of the players
   */
  val players: List[T]
  /**
   * Number of players of the lobby to start a game
   */
  val numberOfPlayers: Int
  /**
   * Check if the current lobby ha enough players to start a game
   *
   * @return boolean
   */
  def hasEnoughPlayers: Boolean
  /**
   * Add a new player to the lobby
   *
   * @return a new lobby with the new player
   */
  def addPlayer(player: T): Lobby[T]
  /**
   * Remove a player from the lobby
   *
   * @return a new lobby without the player
   */
  def removePlayer(playerId: String): Lobby[T]
}