package it.amongsus.server.lobby

import it.amongsus.server.common.Player

/**
 * A single lobby maintaining the list of the user with the same game preferences
 *
 * @param numberOfPlayers number of players required to start a match
 */
case class GameLobby[T <: Player](numberOfPlayers: Int, override val players: List[T] = List()) extends Lobby[T] {
  override def hasEnoughPlayers: Boolean = players.length >= numberOfPlayers

  override def addPlayer(player: T): Lobby[T] = GameLobby(numberOfPlayers, players :+ player)

  override def removePlayer(playerId: String): Lobby[T] =
    GameLobby(numberOfPlayers, players.filter(p => p.id != playerId))
}