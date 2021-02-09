package it.amongsus.messages

object LobbyMessagesClient {
  /**
   * Request of client to connect
   *
   */
  case class ConnectClient(address: String, port: Int)

  /**
   * Successful connection to client
   *
   * @param clientId server generated client id
   */
  case class Connected(clientId: String)

  /**
   * Message sent by the client to join a public lobby for a match with the given number of players
   *
   * @param username username chosen by the user
   * @param numberOfPlayers required to start a match
   */
  case class JoinPublicLobbyClient(username: String, numberOfPlayers: Int)

  /**
   * Message sent by the client to join a private lobby
   *
   * @param username username chosen by the user
   * @param privateLobbyCode required to start a match
   */
  case class JoinPrivateLobbyClient(username: String, privateLobbyCode: String)

  /**
   * Message sent by the client to request a creation of a new private lobby and join
   *
   * @param username username chosen by the user
   * @param numberOfPlayers required to start a match
   */
  case class CreatePrivateLobbyClient(username: String, numberOfPlayers: Int)

  /**
   * Message sent by the client to leave the current lobby
   *
   */
  case class LeaveLobbyClient()

  /**
   * Message sent by the server after a successful lobby connection
   */
  case class UserAddedToLobbyClient()
}
