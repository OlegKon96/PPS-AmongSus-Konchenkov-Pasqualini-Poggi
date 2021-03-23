package it.amongsus.messages

object LobbyMessagesClient {
  /**
   * Client Request to connect
   *
   * @param address the address
   * @param port the port
   */
  case class ConnectClient(address: String, port: Int)
  /**
   * Successful connection to the client
   *
   * @param clientId server generated client id
   */
  case class Connected(clientId: String)
  /**
   * Message sent by the client to join a public lobby for a match with the given number of players
   *
   * @param username the username chosen the user
   * @param numberOfPlayers the number of the players required to start the match
   */
  case class JoinPublicLobbyClient(username: String, numberOfPlayers: Int)
  /**
   * Message sent by the client to join into a private lobby
   *
   * @param username the username chosen by the user
   * @param privateLobbyCode the private lobby code required to start a match
   */
  case class JoinPrivateLobbyClient(username: String, privateLobbyCode: String)
  /**
   * Message sent by the client to request a creation of a new private lobby and join
   *
   * @param username the username chosen by the user
   * @param numberOfPlayers the number of the players required to start the match
   */
  case class CreatePrivateLobbyClient(username: String, numberOfPlayers: Int)
  /**
   * Message sent by the server after private lobby creation
   *
   * @param lobbyCode the lobby code when player create a private lobby
   * @param roomSize the number of the player of the game
   */
  case class PrivateLobbyCreatedClient(lobbyCode: String, roomSize : Int)
  /**
   * Message sent by the client to leave the current lobby
   */
  case object LeaveLobbyClient
  /**
   * Message sent by the server after a successful lobby connection
   *
   * @param numPlayers the number of the players required to start the match
   * @param roomSize the number of the player of the game
   */
  case class UserAddedToLobbyClient(numPlayers: Int, roomSize: Int)
  /**
   * Message sent by the server tu update current number of players in a lobby
   *
   * @param numPlayers the number of the players currently in the lobby
   */
  case class UpdateLobbyClient(numPlayers: Int)
}