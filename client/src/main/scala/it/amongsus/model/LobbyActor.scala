package it.amongsus.model

import akka.actor.{Actor, ActorLogging, Props}
import it.amongsus.messages.GameMessageClient.{LeaveGame, _}
import it.amongsus.messages.GameMessageServer._
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.messages.LobbyMessagesServer._

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object LobbyActor {
  def props(state: LobbyActorInfo): Props =
    Props(new LobbyActor(state))
}

/**
 * Actor responsible for receiving server lobby messages
 *
 * @param state function user to notify back about the received event
 */
class LobbyActor(private val state: LobbyActorInfo) extends Actor
  with ActorLogging {

  override def receive: Receive = defaultBehaviour(state)

  private def defaultBehaviour(state: LobbyActorInfo): Receive = {
    case ConnectClient(address, port) => {
      state.resolveRemoteActorPath(state.generateServerActorPath(address, port)) onComplete {
        case Success(ref) =>
          ref ! ConnectServer(context.self)
        case Failure(t) =>
          println(LobbyJoinErrorEvent(ErrorEvent.ServerNotFound))
      }
    }
    case Connected(id) => context become defaultBehaviour(LobbyActorInfoData(Option(sender), None, id))
    case JoinPublicLobbyClient(username: String, numberOfPlayers: Int) =>
      state.serverRef.get ! JoinPublicLobbyServer(state.clientId, username, numberOfPlayers)
    case CreatePrivateLobbyClient(username: String, numberOfPlayers: Int) =>
      state.serverRef.get ! CreatePrivateLobbyServer(state.clientId, username, numberOfPlayers)
    case JoinPrivateLobbyClient(username: String, privateLobbyCode: String) =>
      state.serverRef.get ! JoinPrivateLobbyServer(state.clientId, username, privateLobbyCode)
    case LeaveLobbyClient =>
      state.serverRef.get ! LeaveLobbyServer(state.clientId)
    case UserAddedToLobby => state.guiRef.get ! UserAddedToLobby
    case PrivateLobbyCreated(lobbyCode) => state.guiRef.get ! PrivateLobbyCreated(lobbyCode)
    case MatchFound(gameRoom) => context become gameBehaviour(GameActorInfo(Option(gameRoom), state.guiRef, state.clientId))
    case LobbyErrorOccurred(error) => error match {
      case LobbyError.PrivateLobbyIdNotValid => ???
      case _ =>
    }
    case m: String => log.debug(m)
  }

  private def gameBehaviour(state: GameActorInfo): Receive = {
    case PlayerReady => state.gameServerRef.get ! Ready(state.clientId, self)

    case LeaveGame => state.gameServerRef.get ! LeaveGame

    case GameWon => state.guiRef.get ! GameWon

    case GameLost => state.guiRef.get ! GameLost

    case GameEndedBecousePlayerLeft => state.guiRef.get ! GameEndedBecousePlayerLeft

    case InvalidPlayerAction => state.guiRef.get ! InvalidPlayerAction

    case GameStateUpdated => GameStateUpdated

    case _ => println("error")
  }
}