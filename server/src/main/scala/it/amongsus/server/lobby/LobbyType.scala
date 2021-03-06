package it.amongsus.server.lobby

sealed class LobbyType(val numberOfPlayers: Int)
/**
 * Minimum numbers of player to start a public lobby of the game
 *
 * @param numberOfPlayers number of players required to start a game
 */
case class PlayerNumberLobby(override val numberOfPlayers: Int) extends LobbyType(numberOfPlayers)
/**
 * Minimum numbers of player to start a private lobby of the game
 *
 * @param lobbyId id used to identify and access the lobby
 * @param numberOfPlayers number of players required to start a game
 */
case class PrivateLobby(lobbyId: String, override val numberOfPlayers: Int) extends LobbyType(numberOfPlayers)