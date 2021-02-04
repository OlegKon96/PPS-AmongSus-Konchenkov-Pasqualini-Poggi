package it.amongsus.messages

import akka.actor.ActorRef

object LobbyMessages {

  /**
   * Request of client to connect
   *
   * @param clientRef reference of the client requesting the connection
   */
  case class Connect(clientRef: ActorRef)

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
  case class JoinPublicLobby(clientId: String, username: String, numberOfPlayers: Int)

  /**
   * Message sent by the client to join a private lobby
   *
   * @param username username chosen by the user
   * @param privateLobbyCode required to start a match
   */
  case class JoinPrivateLobby(clientId: String, username: String, privateLobbyCode: String)

  /**
   * Message sent by the client to request a creation of a new private lobby and join
   *
   * @param username username chosen by the user
   * @param numberOfPlayers required to start a match
   */
  case class CreatePrivateLobby(clientId: String, username: String, numberOfPlayers: Int)

  /**
   * Message sent by the client to leave the current lobby
   *
   * @param clientId id of the user (the one retrieved by the server after the first connection)
   */
  case class LeaveLobby(clientId: String)

  /**
   * Message sent by the server after a successful lobby connection
   */
  case class UserAddedToLobby()

  /**
   * Notify an error during the lobby phase
   *
   * @param error occurred
   */
  case class LobbyErrorOccurred(error: LobbyError)

  /**
   * Message sent by the server after private lobby creation
   *
   * @param lobbyCode code of the created lobby
   */
  case class PrivateLobbyCreated(lobbyCode: String)

  /**
   * Message sent by the server on match found
   *
   * @param gameRoom actorRef of the game room to join
   */
  case class MatchFound(gameRoom: ActorRef)

  /**
   * Error occurred in the lobby phase
   */
  sealed class LobbyError

  object LobbyError {
    case object PrivateLobbyIdNotValid extends LobbyError
    case object InvalidUserId extends LobbyError
  }
}