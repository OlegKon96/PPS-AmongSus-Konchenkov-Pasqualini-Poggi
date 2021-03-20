package it.amongsus.view.actor

import it.amongsus.core.Drawable
import it.amongsus.core.util.MapHelper.GameMap
import it.amongsus.core.map.{Coin, Tile}
import it.amongsus.core.player.Player

object UiActorLobbyMessages {
  /**
   * Initialize Actor
   */
  case object Init
  /**
   * Create a Public Lobby view event
   *
   * @param username the username of the player
   * @param playersNumber the number of players in the public lobby
   */
  case class PublicGameSubmitUi(username: String, playersNumber: Int)
  /**
   * Join in a Private Lobby view event
   *
   * @param username the username of the player
   * @param privateCode the private code of the lobby
   */
  case class PrivateGameSubmitUi(username: String, privateCode: String)
  /**
   * Create a Private Lobby view event
   *
   * @param username the username of the player
   * @param playersNumber the number of players in the private lobby
   */
  case class CreatePrivateGameSubmitUi(username: String, playersNumber: Int)
  /**
   * Leave the Lobby view event
   */
  case object LeaveLobbyUi
  /**
   * Notify User that he was added to a Lobby
   *
   * @param lobbyCode code of the lobby
   * @param roomSize numbers of the lobby's player
   */
  case class PrivateLobbyCreatedUi(lobbyCode: String, roomSize : Int)
  /**
   * Server send a Message to the Ui that there was a successful lobby connection
   *
   * @param numPlayers number of the player of the game
   * @param roomSize numbers of the lobby's player
   */
  case class UserAddedToLobbyUi(numPlayers: Int, roomSize : Int)
  /**
   * Notify the UI Actor that a game is starting
   *
   * @param map of the game
   * @param myChar in the game
   * @param players of the game
   * @param coins of the game
   */
  case class GameFoundUi(map: GameMap, myChar: Player,
                         players: Seq[Player], coins: Seq[Coin])
  /**
   * Notify the User that there's a match to a Lobby
   */
  case object MatchFoundUi
  /**
   * Notify the User that there's an Error Occurred
   */
  case object LobbyErrorOccurredUi
  /**
   * Notify the User that a player close the game
   */
  case object PlayerCloseUi
}