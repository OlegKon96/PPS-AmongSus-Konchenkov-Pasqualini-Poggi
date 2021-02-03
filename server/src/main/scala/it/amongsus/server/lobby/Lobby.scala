package it.amongsus.server.lobby

import akka.japi.Pair
import it.amongsus.server.common.Player

object Lobby {
  def apply(numberOfPlayers: Int): Lobby[Player] = GameLobby[Player](numberOfPlayers)
}

trait Lobby[T <: Player] {

  val players: List[T]

  /**
   * Check if the current lobby ha enough players to start a game
   *
   * @return
   */
  def hasEnoughPlayers: Boolean

  /**
   * Extract the correct number of players to start a match
   *
   * @return the new lobby without the extracted player and the extracted players if present, otherwise none
   */
  def extractPlayersForMatch(): Pair[Lobby[T], Option[Seq[T]]]

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
