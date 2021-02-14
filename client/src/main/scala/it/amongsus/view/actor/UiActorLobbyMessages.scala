package it.amongsus.view.actor


object UiActorLobbyMessages {
  /**
   * Initialize Actor
   */
  case class Init()
  /**
   * Create a Public Lobby view event
   *
   * @param username      the username of the player
   * @param playersNumber the number of players in the public lobby
   */
  case class PublicGameSubmitUi(username: String, playersNumber: Int)
  /**
   * Join in a Private Lobby view event
   *
   * @param username    the username of the player
   * @param privateCode the private code of the lobby
   */
  case class PrivateGameSubmitUi(username: String, privateCode: String)
  /**
   * Create a Private Lobby view event
   *
   * @param username      the username of the player
   * @param playersNumber the number of players in the private lobby
   */
  case class CreatePrivateGameSubmitUi(username: String, playersNumber: Int)
  /**
   * Leave the Lobby view event
   */
  case class LeaveLobbyUi()
  /**
   * Retry the connection to the Server view event
   */
  case class RetryServerConnectionUi()
  /**
   * Notify User that he was added to a Lobby
   */
  case class PrivateLobbyCreatedUi(lobbyCode: String)
  /**
   * Server send a Message to the Ui that there was a successful lobby connection
   */
  case class UserAddedToLobbyUi(numPlayers: Int)
  /**
   * Notify the User that there's a match to a Lobby
   */
  case class GameFoundUi()
  /**
   * Notify the User that there's an Error Occurred
   */
  case class LobbyErrorOccurredUi()
}