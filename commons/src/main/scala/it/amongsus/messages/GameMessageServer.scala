package it.amongsus.messages

import akka.actor.ActorRef
import it.amongsus.core.entities.map.DeadBody
import it.amongsus.core.entities.player.Player
import it.amongsus.core.entities.util.Message

object GameMessageServer {
  /**
   * Client response to the game start message
   *
   * @param playerId           The ID of the player
   * @param gameClientActorRef The Reference of the client actor responsible for the communication with the
   *                           server during the game
   */
  case class PlayerReadyServer(playerId: String, gameClientActorRef: ActorRef)
  /**
   * Tells the server that a player was updated
   *
   * @param player of the game
   * @param gamePlayers of the game
   * @param deadBodys of the game
   */
  case class PlayerMovedServer(player: Player, gamePlayers: Seq[Player], deadBodys: Seq[DeadBody])
  /**
   * Message sent by the client to leave the game
   *
   * @param playerId player identifier
   */
  case class LeaveGameServer(playerId: String)
  /**
   * Message of the error occurred sent by the server to the client
   *
   * @param errorType the type of the error occurred
   */
  case class MatchErrorOccurredServer(errorType: MatchError)
  /**
   * Tells the client to start the session of vote
   *
   * @param players of the game
   */
  case class StartVoting(players: Seq[Player])
  /**
   * Tells to the Ui Actor to send a text messages
   *
   * @param message to send to the server
   * @param char that sends the message
   */
  case class SendTextChatServer(message: Message, char: Player)
  /**
   * Error occurred during the game
   */
  sealed class MatchError

  object MatchError {
    case object PlayerActionNotValid extends MatchError
  }
}