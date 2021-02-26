package it.amongsus.controller.actor

import akka.actor.{Actor, ActorLogging, Props}
import it.amongsus.ActorSystemManager
import it.amongsus.controller.actor.ControllerActorMessages.{ButtonOffController, ButtonOnController, ModelReadyCotroller, MyCharMovedCotroller, UiButtonPressedController, UpdatedMyCharController, UpdatedPlayerController, UpdatedPlayersController}
import it.amongsus.core.entities.util.ButtonType
import it.amongsus.messages.GameMessageClient.{PlayerMovedClient, _}
import it.amongsus.messages.GameMessageServer._
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.messages.LobbyMessagesServer._
import it.amongsus.model.actor.{ModelActor, ModelActorInfo}
import it.amongsus.model.actor.ModelActorMessages.{InitModel, MyCharMovedModel, PlayerMovedModel, UiButtonPressedModel}
import it.amongsus.view.actor.UiActorGameMessages.{ButtonOffUi, ButtonOnUi, _}
import it.amongsus.view.actor.UiActorLobbyMessages.{MatchFoundUi, _}

import scala.concurrent.ExecutionContext.Implicits.global
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
        case Failure(t) =>
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

    case UserAddedToLobbyClient(numPlayers) => state.guiRef.get ! UserAddedToLobbyUi(numPlayers)

    case UpdateLobbyClient(numPlayers) => state.guiRef.get ! UpdateLobbyClient(numPlayers)

    case PrivateLobbyCreatedClient(lobbyCode) => state.guiRef.get ! PrivateLobbyCreatedUi(lobbyCode)

    case MatchFound(gameRoom) =>
      state.guiRef.get ! MatchFoundUi()
      val model =
        ActorSystemManager.actorSystem.actorOf(ModelActor.props(ModelActorInfo(Option(self),
          None, Seq(), Seq(), state.clientId)), "model")
      context become gameBehaviour(GameActorInfo(Option(gameRoom), state.guiRef,
        Option(model), state.clientId))

    case LobbyErrorOccurred(error) => error match {
      case LobbyError.PrivateLobbyIdNotValid => ???
      case _ =>
    }

    case m: String => log.debug(m)
  }

  private def gameBehaviour(state: GameActorInfo): Receive = {
    case PlayerReadyClient() => state.gameServerRef.get ! PlayerReadyServer(state.clientId, self)

    case LeaveGameClient() => state.gameServerRef.get ! LeaveGameServer(state.clientId)

    case GamePlayersClient(players) =>
      state.modelRef.get ! InitModel(state.loadMap(), players)

    case ModelReadyCotroller(map, myChar, players, collectionables) =>
      state.guiRef.get ! GameFoundUi(map, myChar, players, collectionables)

    case MyCharMovedCotroller(direction) => state.modelRef.get ! MyCharMovedModel(direction)

    case PlayerMovedClient(player, deadBodys) => state.modelRef.get ! PlayerMovedModel(player, deadBodys)

    case UpdatedMyCharController(player, gamePLayers, deadBodys) =>
      state.gameServerRef.get ! PlayerMovedServer(player, deadBodys)

    case UpdatedPlayersController(myChar, players, collectionables, deadBodies) =>
      state.guiRef.get ! PlayerUpdatedUi(myChar, players, collectionables, deadBodies)

    case ButtonOnController(button) => state.guiRef.get ! ButtonOnUi(button)

    case ButtonOffController(button) => state.guiRef.get ! ButtonOffUi(button)

    case UiButtonPressedController(button) => state.modelRef.get ! UiButtonPressedModel(button)

    case GameWonClient() => state.guiRef.get ! GameWonUi()

    case GameLostClient() => state.guiRef.get ! GameLostUi()

    case PlayerLeftClient() => state.guiRef.get ! PlayerLeftUi()

    case _ => println("error")
  }
}