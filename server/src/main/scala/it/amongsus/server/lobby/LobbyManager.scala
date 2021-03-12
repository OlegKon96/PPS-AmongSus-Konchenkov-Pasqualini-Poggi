package it.amongsus.server.lobby

import it.amongsus.server.common.{GamePlayer, Player}
import it.amongsus.utils.CustomLogger

trait LobbyManager[T <: Player] {
  /**
   * Map Username-LobbyType of all Players in a Lobby
   */
  var playersToLobby: Map[String, LobbyType] = Map.empty
  /**
   * Map LobbyType-Lobby of lobbies
   */
  var lobbies: Map[LobbyType, Lobby[T]] = Map.empty
  /**
   * Method to get a lobby
   *
   * @param lobbyType type of the lobby to retrieve
   * @return the lobbing corresponding the the specified type, if present
   */
  def getLobby(lobbyType: LobbyType): Option[Lobby[T]]
  /**
   * Return a the lobby of the given player id
   *
   * @param playerId the id of the player in the lobby
   * @return the lobby
   */
  def getLobbyPlayer(playerId: String): Option[Lobby[T]]
}

trait LobbyManagerUtils[T <: Player] extends CustomLogger {
  lobbyManager : LobbyManager[T] =>
  /**
   * Add a player to the lobby system
   *
   * @param player player to be added
   * @param lobbyType type of the lobby
   */
  def addPlayer(player: T, lobbyType: LobbyType): Unit = {
    lobbies = lobbies.get(lobbyType) match {
      case Some(lobby) => lobbies + (lobbyType -> lobby.addPlayer(player))
      case None => lobbies + (lobbyType -> GameLobby(lobbyType.numberOfPlayers).addPlayer(player))
    }
    playersToLobby = playersToLobby + (player.id -> lobbyType)
  }
  /**
   * Remove a player from the lobby system
   *
   * @param playerId the id of the player to remove
   */
  def removePlayer(playerId: String): Unit = {
    playersToLobby.get(playerId) match {
      case Some(lobbyType) =>
        lobbies.get(lobbyType) match {
          case Some(lobby) =>
            lobbies = lobbies + (lobbyType -> lobby.removePlayer(playerId))
          case _ => log(s"lobby of type $lobbyType corresponding to player $playerId  not found")
        }
        playersToLobby = playersToLobby - playerId
      case None => log(s"player $playerId to remove not found")
    }
  }
  /**
   * Tries to extract player from the lobby manager to start a match
   *
   * @param lobbyType type of the lobby
   * @return the list of player to be added to the game
   */
  def attemptExtractPlayerForMatch(lobbyType: LobbyType): Option[Seq[T]] =
    lobbyManager.getLobby(lobbyType).flatMap(lobby => {
      val updatedLobby = lobby.extractPlayersForMatch()
      lobbies = lobbies + (lobbyType -> updatedLobby.first)
      updatedLobby.second
    })
}

case class LobbyManagerImpl[T <: Player]() extends LobbyManager[T] with LobbyManagerUtils[T] {
  override def getLobby(lobbyType: LobbyType): Option[Lobby[T]] = lobbies.get(lobbyType)

  override def getLobbyPlayer(playerId: String): Option[Lobby[T]] = {
    playersToLobby.get(playerId) match {
      case Some(lobbyType) => lobbies.get(lobbyType)
      case None => None
    }
  }
}

object LobbyManager {
  def apply(): LobbyManagerImpl[GamePlayer] = LobbyManagerImpl[GamePlayer]()
}