package it.amongsus.server.lobby

import akka.japi.Pair
import it.amongsus.server.common.Player

/**
 * Rich wrapper of Lobby
 *
 * @param lobby of the game
 * @tparam T generics parameter of player
 */
case class RichLobby[T <: Player](lobby: Lobby[T]) {
  def extractPlayersForMatch(): Pair[Lobby[T], Option[Seq[T]]] = {
    if (lobby.hasEnoughPlayers) {
      Pair(GameLobby(lobby.numberOfPlayers, lobby.players.drop(lobby.numberOfPlayers)),
        Some(lobby.players.take(lobby.numberOfPlayers)))
    } else {
      Pair(lobby, None)
    }
  }
}

object RichLobby {
  implicit def toMyRichLobby[T <: Player](lobby: Lobby[T]): RichLobby[T] = RichLobby[T](lobby)
}
