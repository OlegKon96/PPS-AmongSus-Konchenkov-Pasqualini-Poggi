package it.amongsus.view.actor

import akka.actor.{Actor, ActorLogging, Props}
import it.amongsus.messages.GameMessageClient._
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.view.actor.UiActorGameMessages.{GameLostUi, GameStateUpdatedUi, GameWonUi, InvalidPlayerActionUi, LeaveGameUi, PlayerLeftUi, PlayerReadyUi}
import it.amongsus.view.actor.UiActorLobbyMessages._


object UiActor {
  def props(serverResponsesListener: UiActorInfo): Props = Props(new UiActor(serverResponsesListener))
}

class UiActor(private val serverResponsesListener: UiActorInfo) extends Actor with ActorLogging {

  override def receive: Receive = defaultBehaviour(serverResponsesListener)

  private def defaultBehaviour(state: UiActorInfo): Receive = {
    case Init() => context become defaultBehaviour(UiActorData(Option(sender), None))

    case InitFrame(frame) =>
      context become defaultBehaviour(UiActorData(state.clientRef, Option(frame)))

    case PublicGameSubmitUi(username, playersNumber) =>
      state.clientRef.get ! JoinPublicLobbyClient(username, playersNumber)

    case PrivateGameSubmitUi(username, privateCode) =>
      state.clientRef.get ! JoinPrivateLobbyClient(username, privateCode)

    case CreatePrivateGameSubmitUi(username, playersNumber) =>
      state.clientRef.get ! CreatePrivateLobbyClient(username, playersNumber)

    case LeaveLobbyUi() => state.clientRef.get ! LeaveLobbyClient()

    case RetryServerConnectionUi() => ???

    case UserAddedToLobbyUi() => state.prova()

    case PrivateLobbyCreatedUi(lobbyCode) => ???

    case GameFoundUi() =>
      state.clientRef.get ! PlayerReadyClient()
      context become gameBehaviour(state)

    case LobbyErrorOccurredUi => ???

    case _ => println("ERROR")
  }

  private def gameBehaviour(state: UiActorInfo): Receive = {
    case PlayerReadyUi() => state.clientRef.get ! PlayerReadyClient()

    case LeaveGameUi() => state.clientRef.get ! LeaveGameClient()

    case GameWonUi() => ???

    case GameLostUi() => ???

    case PlayerLeftUi() => ???

    case InvalidPlayerActionUi() => ???

    case GameStateUpdatedUi() => ???

    case _ => println("ERROR")
  }
}