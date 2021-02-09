package it.amongsus.view.actor

import it.amongsus.view.frame.MenuFrame

object UiActorMessages {

  /**
   * Initialize Actor
   */
  case class Init()

  /**
   * Initialize Actor
   */
  case class InitFrame(frame: MenuFrame)

  /**
   * Create a Public Lobby view event
   *
   * @param username      the username of the player
   * @param playersNumber the number of players in the public lobby
   */
  case class PublicGameSubmitUi(username: String, playersNumber: Int)

  /**
   * Join on a Private Lobby view event
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
   * Leave the lobby view event
   */
  case class LeaveLobbyUi()

  /**
   * Retry the connection to server view event
   */
  case class RetryServerConnectionUi()

  /**
   * Notify User that was added to a Lobby
   */
  case class PrivateLobbyCreatedUi(lobbyCode: String)

  /**
   * Notify User that there is a match to a Lobby
   */
  case class GameFoundUi()

  /**
   * Notify User that there's an Error
   */
  case class LobbyErrorOccurredUi()

}
