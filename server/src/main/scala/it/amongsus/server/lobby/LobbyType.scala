package it.amongsus.server.lobby

/**
 *
 * @param numberOfPlayers number of players required to start a game
 */
sealed class LobbyType(val numberOfPlayers: Int)


case class PlayerNumberLobby(override val numberOfPlayers: Int) extends LobbyType(numberOfPlayers)

/**
 * @param lobbyId id used to identify and access the lobby
 */
case class PrivateLobby(lobbyId: String, override val numberOfPlayers: Int) extends LobbyType(numberOfPlayers)
