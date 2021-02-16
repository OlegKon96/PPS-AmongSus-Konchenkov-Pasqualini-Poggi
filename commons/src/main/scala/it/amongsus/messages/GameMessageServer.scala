package it.amongsus.messages

import akka.actor.ActorRef
import it.amongsus.core.entities.player.Player

object GameMessageServer {
  /**
   * Client response to the game start message
   *
   * @param playerId           The ID of the player
   * @param gameClientActorRef The Reference of the client actor responsible for the communication with the server during the game
   */
  case class PlayerReadyServer(playerId: String, gameClientActorRef: ActorRef)
  /**
   * Tells the server that a player was updated
   */
  case class PlayerMovedServer(player: Player)
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
   * Tells the client that the game ended with his victory
   */
  case object GameWonServer
  /**
   * Tells the client that the game ended with a lose
   *
   * @param winnerPlayerName the name of the winner player
   */
  case class GameLostServer(winnerPlayerName: String)
  /**
   * Tells the Client that a player has left the game
   */
  case object PlayerLeftServer
  /**
   * Error occurred during the game
   */
  sealed class MatchError

  object MatchError {
    case object PlayerActionNotValid extends MatchError
  }
}