package it.amongsus.view.actor

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import it.amongsus.ActorSystemManager
import it.amongsus.Constants.Remote.{SERVER_ADDRESS, SERVER_PORT}
import it.amongsus.RichActor.RichContext
import it.amongsus.controller.actor.ControllerActorMessages._
import it.amongsus.core.Drawable
import it.amongsus.core.map.{Coin, DeadBody, Tile}
import it.amongsus.core.player.{AlivePlayer, Player}
import it.amongsus.core.util.MapHelper.GameMap
import it.amongsus.core.util.{ActionType, ChatMessage, Direction, GameEnd}
import it.amongsus.messages.GameMessageClient.{EliminatedPlayer, LeaveGameClient, PlayerReadyClient, VoteClient}
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.view.actor.UiActorGameMessages._
import it.amongsus.view.actor.UiActorLobbyMessages._
import it.amongsus.view.frame.{GameFrame, LobbyFrame, MenuFrame, VoteFrame}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

object UiActor {
  def props(serverResponsesListener: UiLobbyInfo): Props = Props(new UiActor(serverResponsesListener))
}

/**
 * Actor that manages the messages from View Actor
 *
 * @param state the server response listener
 */
class UiActor(private val state: UiLobbyInfo) extends Actor with ActorLogging {
  override def receive: Receive = defaultBehaviour(state)

  private def defaultBehaviour(state: UiLobbyInfo): Receive = {
    case Init => val frame = MenuFrame(Option(self))
      frame.start().unsafeRunSync()
      context >>> defaultBehaviour(UiLobbyInfo(Option(sender), Option(frame)))

    case PublicGameSubmitUi(username: String, playersNumber: Int) =>
      state.clientRef.get ! JoinPublicLobbyClient(username, playersNumber)

    case PrivateGameSubmitUi(username: String, privateCode: String) =>
      state.clientRef.get ! JoinPrivateLobbyClient(username, privateCode)

    case CreatePrivateGameSubmitUi(username: String, playersNumber: Int) =>
      state.clientRef.get ! CreatePrivateLobbyClient(username, playersNumber)

    case UserAddedToLobbyUi(numPlayers: Int, roomSize: Int) =>
      state.currentFrame.get.dispose().unsafeRunSync()
      val lobby = LobbyFrame(self, roomSize)
      lobby.start(numPlayers, state.getCode).unsafeRunSync()
      context >>> defaultBehaviour(UiLobbyInfo(state.clientRef, Option(lobby)))

    case UpdateLobbyClient(numPlayers: Int) => state.updateLobby(numPlayers)

    case PrivateLobbyCreatedUi(lobbyCode: String, roomSize: Int) =>
      state.currentFrame.get.dispose().unsafeRunSync()
      val lobby = LobbyFrame(self, roomSize)
      lobby.start(1, lobbyCode).unsafeRunSync()
      context >>> defaultBehaviour(UiLobbyInfo(state.clientRef, Option(lobby)))
      state.saveCode(lobbyCode)

    case LeaveLobbyUi => state.currentFrame.get.dispose().unsafeRunSync()
      val frame = MenuFrame(Option(self))
      frame.start().unsafeRunSync()
      context >>> defaultBehaviour(UiLobbyInfo(state.clientRef, Option(frame)))
      state.clientRef.get ! LeaveLobbyClient

    case MatchFoundUi => state.showStartButton()

    case PlayerCloseUi => state.clientRef.get ! PlayerLeftController
      self ! PoisonPill

    case PlayerReadyUi => state.clientRef.get ! PlayerReadyClient

    case GameFoundUi(map: GameMap, myChar: Player, players: Seq[Player],
    coins: Seq[Coin]) =>
      state.currentFrame.get.dispose().unsafeRunSync()
      val game = GameFrame(Option(self), map, myChar, players, coins)
      game.start().unsafeRunSync()
      context >>> gameBehaviour(UiGameData(state.clientRef, Option(game)))

    case LobbyErrorOccurredUi => state.lobbyError()

    case _ => log.info("Ui Actor -> lobby error")
  }

  private def gameBehaviour(state: UiGameInfo): Receive = {
    case LeaveGameUi => state.clientRef.get ! LeaveGameClient

    case PlayerCloseUi => state.clientRef.get ! PlayerLeftController
      self ! PoisonPill

    case MyCharMovedUi(direction: Direction) => state.clientRef.get ! MyCharMovedController(direction)

    case PlayerUpdatedUi(myChar: Player, players: Seq[Player], coins: Seq[Coin],
    deadBodies: Seq[DeadBody]) =>
      state.updateGame(myChar, players, coins, deadBodies)

    case ActionOnUi(action: ActionType) => state.setButtonState(action, boolean = true)

    case ActionOffUi(action: ActionType) => state.setButtonState(action, boolean = false)

    case KillTimerUpdateUi(_: Long, seconds: Long) => state.updateKillButton(seconds)

    case SabotageTimerUpdateUi(_: Long, seconds: Long) => state.updateSabotageButton(seconds)

    case GameEndUi(end: GameEnd) => state.clientRef.get ! ConnectClient(SERVER_ADDRESS, SERVER_PORT)
      state.gameFrame.get.dispose().unsafeRunSync()
      state.endGame(state.gameFrame.get.myChar, end)
      context >>> defaultBehaviour(UiLobbyInfo())

    case UiActionTypeUi(button: ActionType) => state.clientRef.get ! UiActionController(button)

    case BeginVotingUi(gamePlayers: Seq[Player]) => val voteFrame = VoteFrame(Option(self), state.gameFrame.get.myChar,
      gamePlayers.filter(p => p.isInstanceOf[AlivePlayer]))
      gamePlayers.find(p => p.clientId == state.gameFrame.get.myChar.clientId).get match {
        case _: AlivePlayer => voteFrame.start().unsafeRunSync()
        case _ => voteFrame.waitVote().unsafeRunSync()
      }
      context >>> voteBehaviour(state, voteFrame)

    case PlayerLeftUi(clientId: String) => log.info("Player -> " + clientId + " left the game.")

    case _ => log.info("Ui Actor -> game error")
  }

  private def voteBehaviour(state: UiGameInfo, voteFrame: VoteFrame): Receive = {
    case VoteUi(username: String) => state.clientRef.get ! VoteClient(username)

    case EliminatedPlayer(username: String) => voteFrame.eliminated(username).unsafeRunSync()
      ActorSystemManager.actorSystem.scheduler.scheduleOnce(3 seconds) {
        self ! RestartGameUi
      }

    case NoOneEliminatedUi => voteFrame.noOneEliminated().unsafeRunSync()
      ActorSystemManager.actorSystem.scheduler.scheduleOnce(3 seconds) {
        self ! RestartGameUi
      }

    case PlayerUpdatedUi(myChar: Player, players: Seq[Player], coins: Seq[Coin],
    deadBodies: Seq[DeadBody]) =>
      state.updateGame(myChar, players, coins, deadBodies)

    case RestartGameUi => voteFrame.dispose().unsafeRunSync()
      state.clientRef.get ! RestartGameController
      context >>> gameBehaviour(state)

    case GameEndUi(end: GameEnd) => state.clientRef.get ! ConnectClient(SERVER_ADDRESS, SERVER_PORT)
      state.gameFrame.get.dispose().unsafeRunSync()
      state.endGame(state.gameFrame.get.myChar, end)
      context >>> defaultBehaviour(UiLobbyInfo())

    case SendTextChatUi(message: ChatMessage, myChar: Player) =>
      state.clientRef.get ! SendTextChatController(message, myChar)

    case ReceiveTextChatUi(message: ChatMessage) =>
      voteFrame.appendTextToChat(message.text, message.username).unsafeRunSync()

    case _ => log.info("Ui Actor -> vote error")
  }
}