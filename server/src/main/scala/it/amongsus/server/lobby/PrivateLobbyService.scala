package it.amongsus.server.lobby

object PrivateLobbyService {
  def apply(): PrivateLobbyService = new PrivateLobbyServiceImpl()
}

/**
 * Manages the creation of private lobbies
 */
trait PrivateLobbyService {

  /**
   * Create a new private lobby
   *
   * @param numberOfPlayers number of player
   * @return the lobby
   */
  def generateNewPrivateLobby(numberOfPlayers: Int): PrivateLobby

  /**
   * Get a private lobby
   *
   * @param lobbyId id of the lobby
   * @return the lobby id
   */
  def retrieveExistingLobby(lobbyId: String): Option[PrivateLobby]

  /**
   * Remove a private lobby
   *
   * @param lobbyId id of the lobby
   */
  def removeLobby(lobbyId: String): Unit
}