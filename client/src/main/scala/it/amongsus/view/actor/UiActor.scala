package it.amongsus.view.actor

import akka.actor.{Actor, ActorLogging, Props}
import it.amongsus.messages.GameMessageClient._
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.view.actor.UiActorGameMessages._
import it.amongsus.view.actor.UiActorLobbyMessages._

object UiActor {
  def props(serverResponsesListener: UiActorInfo): Props = Props(new UiActor(serverResponsesListener))
}

/**
 * Actor that manages the messages from View Actor
 *
 * @param serverResponsesListener the server response listener
 */
class UiActor(private val serverResponsesListener: UiActorInfo) extends Actor with ActorLogging {

  override def receive: Receive = defaultBehaviour(serverResponsesListener)

  private def defaultBehaviour(state: UiActorInfo): Receive = {
    case Init() => context become defaultBehaviour(UiActorData(Option(sender), None, None))

    case InitFrame(menuFrame, lobbyFrame) =>
      context become defaultBehaviour(UiActorData(state.clientRef, Option(menuFrame), Option(lobbyFrame)))

    case PublicGameSubmitUi(username, playersNumber) =>
      state.clientRef.get ! JoinPublicLobbyClient(username, playersNumber)

    case PrivateGameSubmitUi(username, privateCode) =>
      state.clientRef.get ! JoinPrivateLobbyClient(username, privateCode)

    case CreatePrivateGameSubmitUi(username, playersNumber) =>
      state.clientRef.get ! CreatePrivateLobbyClient(username, playersNumber)

    case LeaveLobbyUi() => state.clientRef.get ! LeaveLobbyClient()

    case RetryServerConnectionUi() => ???

    case UserAddedToLobbyUi(numPlayers) => state.updateLobby(numPlayers)

    case NewUserAddedToLobbyClient(numPlayers) => state.toLobby(numPlayers)

    case PrivateLobbyCreatedUi(lobbyCode) => state.saveCode(lobbyCode)

    case GameFoundUi() =>
      state.clientRef.get ! PlayerReadyClient()
      state.toGame()
      context become gameBehaviour(UiGameActorData(Option(sender), None, None))

    case LobbyErrorOccurredUi => state.lobbyError()

    case _ => println("ERROR")
  }

  private def gameBehaviour(state: UiGameActorInfo): Receive = {
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