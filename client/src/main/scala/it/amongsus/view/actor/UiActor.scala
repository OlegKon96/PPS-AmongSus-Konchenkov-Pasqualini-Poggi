package it.amongsus.view.actor

import akka.actor.{Actor, ActorLogging, Props}
import it.amongsus.messages.GameMessageClient._
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.view.actor.UiActorMessages._

object UiActor {
  def props(serverResponsesListener: UiActorInfo): Props = Props(new UiActor(serverResponsesListener))
}

class UiActor(private val serverResponsesListener: UiActorInfo) extends Actor with ActorLogging {

  override def receive: Receive = defaultBehaviour(serverResponsesListener)

  private def defaultBehaviour(state: UiActorInfo): Receive = {
    case Init() => context become defaultBehaviour(UiActorData(Option(sender), None))
    case InitFrame(frame) =>
      context become defaultBehaviour(UiActorData(state.clientRef, Option(frame)))
    case PublicGameSubmitViewEvent(username, playersNumber) =>
      state.clientRef.get ! JoinPublicLobbyClient(username, playersNumber)
    case PrivateGameSubmitViewEvent(username, privateCode) =>
      state.clientRef.get ! JoinPrivateLobbyClient(username, privateCode)
    case CreatePrivateGameSubmitViewEvent(username, playersNumber) =>
      state.clientRef.get ! CreatePrivateLobbyClient(username, playersNumber)
    case LeaveLobbyViewEvent() => state.clientRef.get ! LeaveLobbyClient()
    case RetryServerConnection() => ???
    case UserAddedToLobby() => state.prova()
    case PrivateLobbyCreated(lobbyCode) => ???
    case GameFound =>
      state.clientRef.get ! PlayerReady()
      context become gameBehaviour(state)
    case LobbyErrorOccurred => ???
    case _ => println("ERROR")
  }

  private def gameBehaviour(state: UiActorInfo): Receive = {
    case PlayerReady() => state.clientRef.get ! PlayerReady()
    case LeaveGame() => state.clientRef.get ! LeaveGame()
    case GameWon() => ???
    case GameLost() => ???
    case GameEndedBecousePlayerLeft() => ???
    case InvalidPlayerAction() => ???
    case GameStateUpdated() => ???
    case _ => println("ERROR")
  }
}