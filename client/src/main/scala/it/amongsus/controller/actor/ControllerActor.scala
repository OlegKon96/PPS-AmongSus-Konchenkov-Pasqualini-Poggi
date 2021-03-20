package it.amongsus.controller.actor

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import it.amongsus.ActorSystemManager
import it.amongsus.RichActor.RichContext
import it.amongsus.controller.TimerStatus
import it.amongsus.controller.actor.ControllerActorMessages.{GameEndController, PlayerLeftController}
import it.amongsus.controller.actor.ControllerActorMessages.{SendTextChatController, _}
import it.amongsus.controller.actor.ErrorEvent.LobbyJoinErrorEvent
import it.amongsus.core.util.MapHelper.GameMap
import it.amongsus.core.map.{Coin, DeadBody}
import it.amongsus.core.player.Player
import it.amongsus.core.util.{ActionType, ChatMessage, Direction, GameEnd}
import it.amongsus.messages.GameMessageClient._
import it.amongsus.messages.GameMessageServer._
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.messages.LobbyMessagesServer._
import it.amongsus.model.actor.{ModelActor, ModelGameInfo}
import it.amongsus.model.actor.ModelActorMessages.{GameEndModel, _}
import it.amongsus.view.actor.UiActorGameMessages.{GameEndUi, _}
import it.amongsus.view.actor.UiActorLobbyMessages._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.DurationDouble
import scala.util.{Failure, Success}

object ControllerActor {
  def props(state: ControllerLobbyInfo): Props =
    Props(new ControllerActor(state))
}

/**
 * Actor responsible to receiving the message of the lobby server
 *
 * @param state state of the lobbyActorInfo that represents the function that notify the user about the received event
 */
class ControllerActor(private val state: ControllerLobbyInfo) extends Actor  with ActorLogging {
  override def receive: Receive = lobbyBehaviour(state)

    private def lobbyBehaviour(state: ControllerLobbyInfo): Receive = {
    case ConnectClient(address: String, port: Int) =>
      state.guiRef.get ! Init
      state.resolveRemoteActorPath(state.generateServerActorPath(address, port)) onComplete {
        case Success(ref) =>
          ref ! ConnectServer(context.self)
        case Failure(_) =>
          state.guiRef.get ! LobbyJoinErrorEvent(ErrorEvent.ServerNotFound)
      }
    case Connected(id: String) => context >>> lobbyBehaviour(ControllerLobbyInfoData(Option(sender), state.guiRef, id))

    case JoinPublicLobbyClient(username: String, numberOfPlayers: Int) =>
      state.serverRef.get ! JoinPublicLobbyServer(state.clientId, username, numberOfPlayers)

    case CreatePrivateLobbyClient(username: String, numberOfPlayers: Int) =>
      state.serverRef.get ! CreatePrivateLobbyServer(state.clientId, username, numberOfPlayers)

    case JoinPrivateLobbyClient(username: String, privateLobbyCode: String) =>
      state.serverRef.get ! JoinPrivateLobbyServer(state.clientId, username, privateLobbyCode)

    case LeaveLobbyClient => state.serverRef.get ! LeaveLobbyServer(state.clientId)

    case UserAddedToLobbyClient(numPlayers: Int, roomSize: Int) =>
      state.guiRef.get ! UserAddedToLobbyUi(numPlayers,roomSize)

    case UpdateLobbyClient(numPlayers: Int) => state.guiRef.get ! UpdateLobbyClient(numPlayers)

    case PrivateLobbyCreatedClient(lobbyCode: String,roomSize: Int) =>
      state.guiRef.get ! PrivateLobbyCreatedUi(lobbyCode,roomSize)

    case PlayerLeftController => self ! PoisonPill

    case MatchFound(gameRoom: ActorRef) =>
      state.guiRef.get ! MatchFoundUi
      val model =
        ActorSystemManager.actorSystem.actorOf(ModelActor.props(ModelGameInfo(Option(self),
          None, Seq(), Seq(), state.clientId)), "model")
      context >>> gameBehaviour(GameActorInfo(Option(gameRoom), state.guiRef,
        Option(model), state.clientId))

    case TestGameBehaviour(model: ActorRef, server: ActorRef) =>
      context >>> gameBehaviour(GameActorInfo(Option(server), state.guiRef,
        Option(model), state.clientId))

    case LobbyErrorOccurred(error: LobbyError) => error match {
      case LobbyError.PrivateLobbyIdNotValid =>  log.info("Controller Actor -> Private Lobby id not valid" )
      case _ =>
    }

    case _ => log.info("Controller Actor -> lobby error" )
  }

  private def gameBehaviour(state: GameActorInfo): Receive = {
    case PlayerReadyClient => state.gameServerRef.get ! PlayerReadyServer(state.clientId, self)

    case GamePlayersClient(players: Seq[Player]) =>
      state.modelRef.get ! InitModel(state.loadMap(), players)

    case ModelReadyController(map: GameMap, myChar: Player, players: Seq[Player],
    coins: Seq[Coin]) => state.guiRef.get ! GameFoundUi(map, myChar, players, coins)

    case KillTimerController(status: TimerStatus) => state.manageKillTimer(status)

    case MyCharMovedController(direction: Direction) => state.modelRef.get ! MyCharMovedModel(direction)

    case PlayerMovedClient(player: Player, deadBodies: Seq[DeadBody]) =>
      state.modelRef.get ! PlayerMovedModel(player, deadBodies)

    case UpdatedMyCharController(player: Player, gamePlayers: Seq[Player], deadBodies: Seq[DeadBody]) =>
      state.gameServerRef.get ! PlayerMovedServer(player, gamePlayers, deadBodies)

    case UpdatedPlayersController(myChar: Player, players: Seq[Player],coins: Seq[Coin],
    deadBodies: Seq[DeadBody]) => state.guiRef.get ! PlayerUpdatedUi(myChar, players, coins, deadBodies)

    case ActionOnController(action: ActionType) => state.guiRef.get ! ActionOnUi(action)

    case ActionOffController(action: ActionType) => state.guiRef.get ! ActionOffUi(action)

    case UiActionController(action: ActionType) => state.modelRef.get ! UiActionModel(action)
      state.checkButton(action)

    case BeginVotingController(gamePlayers: Seq[Player]) => state.gameServerRef.get ! StartVoting(gamePlayers)
      state.guiRef.get ! BeginVotingUi(gamePlayers)
      context >>> voteBehaviour(state)

    case StartVotingClient(gamePlayers: Seq[Player]) => state.modelRef.get ! BeginVotingModel
      state.guiRef.get ! BeginVotingUi(gamePlayers)
      context >>> voteBehaviour(state)

    case GameEndController(end: GameEnd) => state.guiRef.get ! GameEndUi(end)
      context >>> lobbyBehaviour(ControllerLobbyInfo(state.guiRef))

    case GameEndClient(end: GameEnd) => state.modelRef.get ! GameEndModel(end)

    case PlayerLeftController => state.modelRef.get ! MyPlayerLeftModel
      self ! PoisonPill

    case LeaveGameClient => state.gameServerRef.get ! LeaveGameServer(state.clientId)

    case PlayerLeftClient(clientId: String) => state.modelRef.get ! PlayerLeftModel(clientId)
      state.guiRef.get ! PlayerLeftUi(clientId)

    case _ => log.info("Controller Actor -> game error" )
  }

  private def voteBehaviour(state: GameActorInfo): Receive = {
    case VoteClient(username) => state.gameServerRef.get ! VoteClient(username)

    case EliminatedPlayer(username: String) =>
      state.modelRef.get ! KillPlayerModel(username)
      state.guiRef.get ! EliminatedPlayer(username)

    case NoOneEliminatedController => state.guiRef.get ! NoOneEliminatedUi

    case UpdatedPlayersController(myChar: Player, players: Seq[Player], coins: Seq[Coin],
    deadBodies: Seq[DeadBody]) => state.guiRef.get ! PlayerUpdatedUi(myChar, players, coins, deadBodies)

    case SendTextChatController(message: ChatMessage, myChar: Player) =>
      state.gameServerRef.get ! SendTextChatServer(message, myChar)

    case SendTextChatClient(message: ChatMessage) => state.guiRef.get ! ReceiveTextChatUi(message)

    case GameEndController(end: GameEnd) => state.guiRef.get ! GameEndUi(end)
      context >>> lobbyBehaviour(ControllerLobbyInfo(state.guiRef))

    case GameEndClient(end: GameEnd) =>
      ActorSystemManager.actorSystem.scheduler.scheduleOnce(3.1 seconds){
        state.modelRef.get ! GameEndModel(end)
      }

    case RestartGameController =>
      state.modelRef.get ! RestartGameModel
      context >>> gameBehaviour(state)

    case PlayerLeftController => state.modelRef.get ! MyPlayerLeftModel
      self ! PoisonPill

    case PlayerLeftClient(clientId: String) => state.guiRef.get ! PlayerLeftUi(clientId)

    case _ => log.info("Controller Actor -> vote error" )
  }
}