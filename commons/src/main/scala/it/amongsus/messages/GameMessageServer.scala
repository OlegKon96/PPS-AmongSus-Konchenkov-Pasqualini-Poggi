package it.amongsus.messages

import akka.actor.ActorRef

object GameMessageServer {

  /**
   * Client response to the game start message
   *
   * @param playerId           id of the player
   * @param gameClientActorRef ref of the client actor responsible for the communication with the server during the game
   */
  case class Ready(playerId: String, gameClientActorRef: ActorRef)

  /**
   * Message sent by the client to leave the game
   *
   * @param playerId player identifier
   */
  case class LeaveGame(playerId: String)

  /**
   * an error sent by the server to the client
   *
   * @param errorType
   */
  case class MatchErrorOccurred(errorType: MatchError)

  /**
   * Tells the client the game ended with his victory
   */
  case object Won

  /**
   * Tells the client the game ended with a lost
   *
   * @param winnerPlayerName the name of the winner player
   */
  case class Lost(winnerPlayerName: String)


  case object GameEndedForPlayerLeft


  /**
   * Error occurred during a game
   */
  sealed class MatchError

  object MatchError {

    case object PlayerActionNotValid extends MatchError

  }


}
