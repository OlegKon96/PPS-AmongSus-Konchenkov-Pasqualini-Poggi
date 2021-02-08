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
   * @param username the username of the player
   * @param playersNumber the number of players in the public lobby
   */
  case class PublicGameSubmitViewEvent(username: String, playersNumber: Int)

  /**
   * Join on a Private Lobby view event
   *
   * @param username the username of the player
   * @param privateCode the private code of the lobby
   */
  case class PrivateGameSubmitViewEvent(username: String, privateCode: String)

  /**
   * Create a Private Lobby view event
   *
   * @param username the username of the player
   * @param playersNumber the number of players in the private lobby
   */
  case class CreatePrivateGameSubmitViewEvent(username: String, playersNumber: Int)

  /**
   * Leave the lobby view event
   */
  case class LeaveLobbyViewEvent()

  /**
   * Retry the connection to server view event
   */
  case class RetryServerConnection()

  /**
   * Notify User that was added to a Lobby
   */
  case class UserAddedToLobby()

  /**
   * Notify User that was added to a Lobby
   */
  case class PrivateLobbyCreated(lobbyCode: String)

  /**
   * Notify User that there is a match to a Lobby
   */
  case class GameFound()

  /**
   * Notify User that there's an Error
   */
  case object LobbyErrorOccurred
