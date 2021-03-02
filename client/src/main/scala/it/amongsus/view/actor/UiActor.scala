package it.amongsus.view.actor

import akka.actor.{Actor, ActorLogging, Props}
import it.amongsus.Constants
import it.amongsus.controller.actor.ControllerActorMessages.{MyCharMovedCotroller, RestartGameController, UiButtonPressedController}
import it.amongsus.core.entities.player.{AlivePlayer, Crewmate, Impostor, Player}
import it.amongsus.core.entities.util.ButtonType
import it.amongsus.core.entities.util.GameEnd.{Lost, Win}
import it.amongsus.messages.GameMessageClient._
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.view.actor.UiActorGameMessages._
import it.amongsus.view.actor.UiActorLobbyMessages._
import it.amongsus.view.frame.{GameFrame, LobbyFrame, MenuFrame, VoteFrame, WinFrame}

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
    case Init() =>
      val frame = MenuFrame(Option(self))
      frame.start().unsafeRunSync()
      context become defaultBehaviour(UiActorData(Option(sender), Option(frame)))

    case PublicGameSubmitUi(username, playersNumber) =>
      state.clientRef.get ! JoinPublicLobbyClient(username, playersNumber)

    case PrivateGameSubmitUi(username, privateCode) =>
      state.clientRef.get ! JoinPrivateLobbyClient(username, privateCode)

    case CreatePrivateGameSubmitUi(username, playersNumber) =>
      state.clientRef.get ! CreatePrivateLobbyClient(username, playersNumber)

    case LeaveLobbyUi() =>
      state.currentFrame.get.dispose().unsafeRunSync()
      val frame = MenuFrame(Option(self))
      frame.start().unsafeRunSync()
      context become defaultBehaviour(UiActorData(state.clientRef, Option(frame)))
      state.clientRef.get ! LeaveLobbyClient()

    case RetryServerConnectionUi() => ???

    case UserAddedToLobbyUi(numPlayers) =>
      state.currentFrame.get.dispose().unsafeRunSync()
      val lobby = LobbyFrame(self)
      lobby.start(numPlayers, state.getCode()).unsafeRunSync()
      context become defaultBehaviour(UiActorData(state.clientRef, Option(lobby)))

    case UpdateLobbyClient(numPlayers) => state.updateLobby(numPlayers)

    case PrivateLobbyCreatedUi(lobbyCode) =>
      state.currentFrame.get.dispose().unsafeRunSync()
      val lobby = LobbyFrame(self)
      lobby.start(1, lobbyCode).unsafeRunSync()
      context become defaultBehaviour(UiActorData(state.clientRef, Option(lobby)))
      state.saveCode(lobbyCode)

    case MatchFoundUi() => state.clientRef.get ! PlayerReadyClient()

    case GameFoundUi(map, myChar, players, collectionables) =>
      state.currentFrame.get.dispose().unsafeRunSync()
      val game = GameFrame(Option(self),map,myChar,players,collectionables)
      game.start().unsafeRunSync()
      context become gameBehaviour(UiGameActorData(state.clientRef, Option(game)))

    case LobbyErrorOccurredUi => state.lobbyError()

    case _ => println("ERROR")
  }

  private def gameBehaviour(state: UiGameActorInfo): Receive = {
    case PlayerReadyUi() => state.clientRef.get ! PlayerReadyClient()

    case LeaveGameUi() => state.clientRef.get ! LeaveGameClient()

    case MyCharMovedUi(direction) => state.clientRef.get ! MyCharMovedCotroller(direction)

    case PlayerUpdatedUi(myChar, players, collectionables, deadBodies) => state.updatePlayer(players,collectionables)

    case ButtonOnUi(button) => state.enableButton(button,true)

    case ButtonOffUi(button) => state.enableButton(button,false)

    case KillTimerUpdateUi(minutes: Long, seconds: Long) => //TODO implement view change

    case UiButtonPressedUi(button) => state.clientRef.get ! UiButtonPressedController(button)

    case BeginVotingUi(gamePlayers: Seq[Player]) =>  //TODO VoteFrame startUp
      context become voteBehaviour(state)

    case GameEndUi(end) => //win message(end)
      state.clientRef.get ! ConnectClient(Constants.Remote.SERVER_ADDRESS, Constants.Remote.SERVER_PORT)
      state.gameFrame.get.dispose().unsafeRunSync()
      context become defaultBehaviour(UiActorInfo())

    case _ => println("ERROR")
  }

  private def voteBehaviour(state: UiGameActorInfo): Receive = {
    case RestartGameUi() =>
      state.clientRef.get ! RestartGameController()
      context become gameBehaviour(state)
  }
}