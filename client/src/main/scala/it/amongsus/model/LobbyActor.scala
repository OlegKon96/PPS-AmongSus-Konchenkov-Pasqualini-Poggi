package it.amongsus.client.model.lobby

import akka.actor.{Actor, ActorLogging, Props}
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.messages.LobbyMessagesServer._
import it.amongsus.model.{ErrorEvent, LobbyActorInfo, LobbyActorInfoData}

import scala.util.{Failure, Success}
import scala.concurrent.ExecutionContext.Implicits.global

object LobbyActor {
  def props(serverResponsesListener: LobbyActorInfo): Props =
    Props(new LobbyActor(serverResponsesListener))
}

/**
 * Actor responsible for receiving server lobby messages
 *
 * @param serverResponsesListener function user to notify back about the received event
 */
class LobbyActor(private val serverResponsesListener: LobbyActorInfo) extends Actor
  with ActorLogging {

  override def receive: Receive = defaultBehaviour(serverResponsesListener)

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
    case LeaveLobbyClient() =>
      state.serverRef.get ! LeaveLobbyServer(state.clientId)
    case UserAddedToLobby() => println("user added to lobby")
    case PrivateLobbyCreated(lobbyCode) => ???
    case MatchFound(gameRoom) => ???
    case LobbyErrorOccurred(error) => error match {
      case LobbyError.PrivateLobbyIdNotValid => ???
      case _ =>
    }
    case m: String => log.debug(m)
  }
}