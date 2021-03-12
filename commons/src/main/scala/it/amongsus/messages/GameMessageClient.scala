package it.amongsus.messages

import it.amongsus.core.map.DeadBody
import it.amongsus.core.player.Player
import it.amongsus.core.util.{GameEnd, ChatMessage}

object GameMessageClient {
  /**
   * Tells the controller actor that a player is ready to start a game
   */
  case class PlayerReadyClient()
  /**
   * Tells the controller actor that the list of players of a game
   *
   * @param players of the game
   */
  case class GamePlayersClient(players : Seq[Player])
  /**
   * Tells the controller actor that player moved to another position
   *
   * @param player of the game
   * @param deadBodys of the game
   */
  case class PlayerMovedClient(player: Player, deadBodys: Seq[DeadBody])
  /**
   * Tells the controller actor that a player wants to leave the game
   */
  case class LeaveGameClient()
  /**
   * Tells the controller actor that the game is ended with a win
   *
   * @param end info of the game
   */
  case class GameEndClient(end: GameEnd)
  /**
   * Tells the controller actor that a button is pressed
   *
   * @param players of the game
   */
  case class StartVotingClient(players: Seq[Player])
  /**
   * Tells the controller actor that the player left the game
   *
   * @param clientId of the player
   */
  case class PlayerLeftClient(clientId: String)
  /**
   * Tells the controller actor that Player skip the vote
   */
  case class SkipVoteClient()
  /**
   * Tells the controller actor that Player vote another player
   *
   * @param username of the player voted
   */
  case class VoteClient(username: String)
  /**
   * Tells the controller actor that a Player is eliminated
   *
   * @param username of the player eliminated
   */
  case class EliminatedPlayer(username: String)
  /**
   * Tells the controller actor to update the vote
   *
   * @param username of the player voted
   */
  case class UpdatedVoteServer(username: String)
  /**
   * Tells to the Client Actor to send a text messages
   *
   * @param message to send to the client
   */
  case class SendTextChatClient(message: ChatMessage)
  /**
   * Tells to the Client Actor that no one was ejected from the vote session
   */
  case class NoOneEliminatedController()
}