package it.amongsus.controller.actor

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import it.amongsus.ActorSystemManager
import it.amongsus.controller.actor.ControllerActorMessages.{GameEndController, PlayerLeftController}
import it.amongsus.controller.actor.ControllerActorMessages.{SendTextChatController, _}
import it.amongsus.core.entities.player.Player
import it.amongsus.messages.GameMessageClient._
import it.amongsus.messages.GameMessageServer.{PlayerMovedServer, PlayerReadyServer, SendTextChatServer, StartVoting}
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.messages.LobbyMessagesServer._
import it.amongsus.model.actor.{ModelActor, ModelActorInfo}
import it.amongsus.model.actor.ModelActorMessages.{GameEndModel, _}
import it.amongsus.view.actor.UiActorGameMessages.{GameEndUi, _}
import it.amongsus.view.actor.UiActorLobbyMessages._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationDouble
import scala.util.{Failure, Success}

object ControllerActor {
  def props(state: LobbyActorInfo): Props =
    Props(new ControllerActor(state))
}

/**
 * Actor responsible to receiving the message of the lobby server
 *
 * @param state state of the lobbyActorInfo that represents the function that notify the user about the received event
 */
class ControllerActor(private val state: LobbyActorInfo) extends Actor  with ActorLogging {

  override def receive: Receive = lobbyBehaviour(state)

  private def lobbyBehaviour(state: LobbyActorInfo): Receive = {
    case ConnectClient(address, port) =>
      state.guiRef.get ! Init()
      state.resolveRemoteActorPath(state.generateServerActorPath(address, port)) onComplete {
        case Success(ref) =>
          ref ! ConnectServer(context.self)
        case Failure(_) =>
          state.guiRef.get ! LobbyJoinErrorEvent(ErrorEvent.ServerNotFound)
      }
    case Connected(id) => context become lobbyBehaviour(LobbyActorInfoData(Option(sender), state.guiRef, id))

    case JoinPublicLobbyClient(username: String, numberOfPlayers: Int) =>
      state.serverRef.get ! JoinPublicLobbyServer(state.clientId, username, numberOfPlayers)

    case CreatePrivateLobbyClient(username: String, numberOfPlayers: Int) =>
      state.serverRef.get ! CreatePrivateLobbyServer(state.clientId, username, numberOfPlayers)

    case JoinPrivateLobbyClient(username: String, privateLobbyCode: String) =>
      state.serverRef.get ! JoinPrivateLobbyServer(state.clientId, username, privateLobbyCode)

    case LeaveLobbyClient() => state.serverRef.get ! LeaveLobbyServer(state.clientId)

    case UserAddedToLobbyClient(numPlayers, roomSize) => state.guiRef.get ! UserAddedToLobbyUi(numPlayers,roomSize)

    case UpdateLobbyClient(numPlayers) => state.guiRef.get ! UpdateLobbyClient(numPlayers)

    case PrivateLobbyCreatedClient(lobbyCode,roomSize) => state.guiRef.get ! PrivateLobbyCreatedUi(lobbyCode,roomSize)

    case PlayerLeftController() => self ! PoisonPill

    case MatchFound(gameRoom) =>
      state.guiRef.get ! MatchFoundUi()
      val model =
        ActorSystemManager.actorSystem.actorOf(ModelActor.props(ModelActorInfo(Option(self),
          None, Seq(), Seq(), state.clientId)), "model")
      context become gameBehaviour(GameActorInfo(Option(gameRoom), state.guiRef,
        Option(model), state.clientId))

    case TestGameBehaviour(model, server) =>
      context become gameBehaviour(GameActorInfo(Option(server), state.guiRef,
        Option(model), state.clientId))

    case LobbyErrorOccurred(error) => error match {
      case LobbyError.PrivateLobbyIdNotValid => ???
      case _ =>
    }

    case _ => println("lobby error" + _)
  }

  private def gameBehaviour(state: GameActorInfo): Receive = {
    case PlayerReadyClient() => state.gameServerRef.get ! PlayerReadyServer(state.clientId, self)

    case GamePlayersClient(players) =>
      state.modelRef.get ! InitModel(state.loadMap(), players)

    case ModelReadyController(map, myChar, players, collectionables) =>
      state.guiRef.get ! GameFoundUi(map, myChar, players, collectionables)

    case KillTimerController(status) => state.manageKillTimer(status)

    case MyCharMovedController(direction) => state.modelRef.get ! MyCharMovedModel(direction)

    case PlayerMovedClient(player, deadBodys) => state.modelRef.get ! PlayerMovedModel(player, deadBodys)

    case UpdatedMyCharController(player, gamePlayers, deadBodys) =>
      state.gameServerRef.get ! PlayerMovedServer(player, gamePlayers, deadBodys)

    case UpdatedPlayersController(myChar, players, collectionables, deadBodies) =>
      state.guiRef.get ! PlayerUpdatedUi(myChar, players, collectionables, deadBodies)

    case ButtonOnController(button) => state.guiRef.get ! ButtonOnUi(button)

    case ButtonOffController(button) => state.guiRef.get ! ButtonOffUi(button)

    case UiButtonPressedController(button) => state.modelRef.get ! UiButtonPressedModel(button)
      state.checkButton(button)

    case BeginVotingController(gamePlayers: Seq[Player]) => state.gameServerRef.get ! StartVoting(gamePlayers)
      state.guiRef.get ! BeginVotingUi(gamePlayers)
      context become voteBehaviour(state)

    case StartVotingClient(gamePlayers: Seq[Player]) => state.modelRef.get ! BeginVotingModel()
      state.guiRef.get ! BeginVotingUi(gamePlayers)
      context become voteBehaviour(state)

    case GameEndController(end) => state.guiRef.get ! GameEndUi(end)
      context become lobbyBehaviour(LobbyActorInfo(state.guiRef))

    case GameEndClient(end) => state.modelRef.get ! GameEndModel(end)

    case PlayerLeftController() => state.modelRef.get ! MyPlayerLeftModel()
      self ! PoisonPill

    //case LeaveGameClient() => state.gameServerRef.get ! LeaveGameServer(state.clientId)

    case PlayerLeftClient(clientId) => state.modelRef.get ! PlayerLeftModel(clientId)
      state.guiRef.get ! PlayerLeftUi(clientId)
  }

  private def voteBehaviour(state: GameActorInfo): Receive = {
    case VoteClient(username) => state.gameServerRef.get ! VoteClient(username)

    case EliminatedPlayer(username) =>
      state.modelRef.get ! KillPlayerModel(username)
      state.guiRef.get ! EliminatedPlayer(username)

    case NoOneEliminatedController() => state.guiRef.get ! NoOneEliminatedUi()

    case UpdatedPlayersController(myChar, players, collectionables, deadBodies) =>
      state.guiRef.get ! PlayerUpdatedUi(myChar, players, collectionables, deadBodies)

    case SendTextChatController(message, myChar) => state.gameServerRef.get ! SendTextChatServer(message, myChar)

    case SendTextChatClient(message) => state.guiRef.get ! ReceiveTextChatUi(message)

    case GameEndController(end) => state.guiRef.get ! GameEndUi(end)
      context become lobbyBehaviour(LobbyActorInfo(state.guiRef))

    case GameEndClient(end) =>
      ActorSystemManager.actorSystem.scheduler.scheduleOnce(3.1 seconds){
        state.modelRef.get ! GameEndModel(end)
      }

    case RestartGameController() =>
      state.modelRef.get ! RestartGameModel()
      context become gameBehaviour(state)

    case PlayerLeftController() => state.modelRef.get ! MyPlayerLeftModel()
      self ! PoisonPill

    case PlayerLeftClient(clientId) => state.guiRef.get ! PlayerLeftUi(clientId)

    case _ => println("Error Controller Vote")
  }
}