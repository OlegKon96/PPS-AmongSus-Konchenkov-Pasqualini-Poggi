package it.amongsus.view.actor

import it.amongsus.ActorSystemManager
import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import it.amongsus.Constants
import it.amongsus.controller.actor.ControllerActorMessages.{MyCharMovedCotroller, PlayerLeftController, RestartGameController, SendTextChatController, UiButtonPressedController}
import it.amongsus.core.entities.player.{AlivePlayer, Crewmate, Impostor, Player}
import it.amongsus.core.entities.util.GameEnd.{Lost, Win}
import it.amongsus.messages.GameMessageClient.{EliminatedPlayer, LeaveGameClient, PlayerReadyClient}
import it.amongsus.messages.GameMessageClient.VoteClient
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.view.actor.UiActorGameMessages.KillTimerUpdateUi
import it.amongsus.view.actor.UiActorGameMessages._
import it.amongsus.view.actor.UiActorLobbyMessages._
import it.amongsus.view.frame.{GameFrame, LobbyFrame, MenuFrame, VoteFrame, WinFrame}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._

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
      context become defaultBehaviour(UiActorInfo(Option(sender), Option(frame)))

    case PublicGameSubmitUi(username, playersNumber) =>
      state.clientRef.get ! JoinPublicLobbyClient(username, playersNumber)

    case PrivateGameSubmitUi(username, privateCode) =>
      state.clientRef.get ! JoinPrivateLobbyClient(username, privateCode)

    case CreatePrivateGameSubmitUi(username, playersNumber) =>
      state.clientRef.get ! CreatePrivateLobbyClient(username, playersNumber)

    case UserAddedToLobbyUi(numPlayers,roomSize) =>
      state.currentFrame.get.dispose().unsafeRunSync()
      val lobby = LobbyFrame(self,roomSize)
      lobby.start(numPlayers, state.getCode).unsafeRunSync()
      context become defaultBehaviour(UiActorInfo(state.clientRef, Option(lobby)))

    case UpdateLobbyClient(numPlayers) => state.updateLobby(numPlayers)

    case RetryServerConnectionUi() => ???

    case PrivateLobbyCreatedUi(lobbyCode, roomSize) =>
      state.currentFrame.get.dispose().unsafeRunSync()
      val lobby = LobbyFrame(self,roomSize)
      lobby.start(1, lobbyCode).unsafeRunSync()
      context become defaultBehaviour(UiActorInfo(state.clientRef, Option(lobby)))
      state.saveCode(lobbyCode)

    case LeaveLobbyUi() =>
      state.currentFrame.get.dispose().unsafeRunSync()
      val frame = MenuFrame(Option(self))
      frame.start().unsafeRunSync()
      context become defaultBehaviour(UiActorInfo(state.clientRef, Option(frame)))
      state.clientRef.get ! LeaveLobbyClient()

    case MatchFoundUi() => state.showStartButton()

    case PlayerCloseUi() => state.clientRef.get ! PlayerLeftController()
      self ! PoisonPill

    case PlayerReadyUi() => state.clientRef.get ! PlayerReadyClient()

    case GameFoundUi(map, myChar, players, collectionables) =>
      state.currentFrame.get.dispose().unsafeRunSync()
      val game = GameFrame(Option(self),map,myChar,players,collectionables)
      game.start().unsafeRunSync()
      context become gameBehaviour(UiGameActorData(state.clientRef, Option(game)))

    case LobbyErrorOccurredUi => state.lobbyError()

    case _ => println("ERROR")
  }

  private def gameBehaviour(state: UiGameActorInfo): Receive = {

    case LeaveGameUi() => state.clientRef.get ! LeaveGameClient()

    case PlayerCloseUi() => state.clientRef.get ! PlayerLeftController()
      self ! PoisonPill

    case MyCharMovedUi(direction) => state.clientRef.get ! MyCharMovedCotroller(direction)

    case PlayerUpdatedUi(myChar, players, collectionables, deadBodies) =>
      state.updatePlayer(myChar,players,collectionables, deadBodies)

    case ButtonOnUi(button) => state.enableButton(button,boolean = true)

    case ButtonOffUi(button) => state.enableButton(button, boolean = false)

    case KillTimerUpdateUi(minutes: Long, seconds: Long) => //state.updateKillButton(seconds)

    case SabotageTimerUpdateUi(minutes: Long, seconds: Long) => //state.updateSabotageButton(seconds)

    case GameEndUi(end) => //win message(end)
      state.clientRef.get ! ConnectClient(Constants.Remote.SERVER_ADDRESS, Constants.Remote.SERVER_PORT)
      state.gameFrame.get.dispose().unsafeRunSync()
      end match {
        case Win() => WinFrame(Option(self), state.gameFrame.get.myChar).start().unsafeRunSync()
        case Lost() => state.gameFrame.get.myChar match {
          case _: Crewmate => WinFrame(Option(self),
            state.gameFrame.get.players.find(p => !p.isInstanceOf[Impostor]).get).start().unsafeRunSync()
          case _: Impostor => WinFrame(Option(self),
            state.gameFrame.get.players.find(p => !p.isInstanceOf[Crewmate]).get).start().unsafeRunSync()
        }
      }
      context become defaultBehaviour(UiActorInfo())

    case UiButtonPressedUi(button) => state.clientRef.get ! UiButtonPressedController(button)

    case BeginVotingUi(gamePlayers: Seq[Player]) =>  val voteFrame = VoteFrame(Option(self), state.gameFrame.get.myChar,
      gamePlayers.filter(p => p.isInstanceOf[AlivePlayer]))
      gamePlayers.find(p => p.clientId==state.gameFrame.get.myChar.clientId).get match {
        case _: AlivePlayer => voteFrame.start().unsafeRunSync()
        case _ => voteFrame.waitVote().unsafeRunSync()
      }
      context become voteBehaviour(state, voteFrame)

    case PlayerLeftUi(clientId) => println("left -> " + clientId)

    case _ => println("ERROR 2")
  }

  private def voteBehaviour(state: UiGameActorInfo, voteFrame : VoteFrame): Receive = {
    case VoteUi(username) => state.clientRef.get ! VoteClient(username)

    case EliminatedPlayer(username) =>
      voteFrame.eliminated(username).unsafeRunSync()
      ActorSystemManager.actorSystem.scheduler.scheduleOnce(3 seconds){
        self ! RestartGameUi()
      }

    case NoOneEliminatedUi() =>
      voteFrame.noOneEliminated().unsafeRunSync()
      ActorSystemManager.actorSystem.scheduler.scheduleOnce(3 seconds){
        self ! RestartGameUi()
      }

    case PlayerUpdatedUi(myChar, players, collectionables, deadBodies) =>
      state.updatePlayer(myChar,players,collectionables, deadBodies)

    case RestartGameUi() =>
      voteFrame.dispose().unsafeRunSync()
      state.clientRef.get ! RestartGameController()
      context become gameBehaviour(state)

    case GameEndUi(end) => //win message(end)
      state.clientRef.get ! ConnectClient(Constants.Remote.SERVER_ADDRESS, Constants.Remote.SERVER_PORT)
      state.gameFrame.get.dispose().unsafeRunSync()
      end match {
        case Win() => WinFrame(Option(self), state.gameFrame.get.myChar).start().unsafeRunSync()
        case Lost() => state.gameFrame.get.myChar match {
          case _: Crewmate => WinFrame(Option(self),
            state.gameFrame.get.players.find(p => !p.isInstanceOf[Impostor]).get).start().unsafeRunSync()
          case _: Impostor => WinFrame(Option(self),
            state.gameFrame.get.players.find(p => !p.isInstanceOf[Crewmate]).get).start().unsafeRunSync()
        }
      }
      context become defaultBehaviour(UiActorInfo())

    case SendTextChatUi(message, myChar) => state.clientRef.get ! SendTextChatController(message, myChar)
      println("UiActor: "+myChar.username)

    case ReceiveTextChatUi(message) => voteFrame.appendTextToChat(message.text, message.username).unsafeRunSync()

    case ReceiveTextChatGhostUi(message) =>
      voteFrame.appendTextToChatGhost(message.text, message.username).unsafeRunSync()

    case _ => println("Error vote behaviour")
  }
}