package it.amongsus.model.actor

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import it.amongsus.ActorSystemManager
import it.amongsus.RichActor.RichContext
import it.amongsus.controller.ActionTimer.{TimerEnded, TimerStarted}
import it.amongsus.controller.TimerStatus
import it.amongsus.controller.actor.ControllerActorMessages.{ActionOffController, BeginVotingController}
import it.amongsus.controller.actor.ControllerActorMessages.{GameEndController, ModelReadyController}
import it.amongsus.controller.actor.ControllerActorMessages.UpdatedPlayersController
import it.amongsus.core.map.DeadBody
import it.amongsus.core.map.MapHelper.{generateCoins, generateMap}
import it.amongsus.core.player.Player
import it.amongsus.core.util.ActionType.{EmergencyAction, KillAction, ReportAction, SabotageAction, VentAction}
import it.amongsus.core.util.{ActionType, Direction, GameEnd}
import it.amongsus.model.actor.ModelActorMessages.{BeginVotingModel, GameEndModel, InitModel, KillPlayerModel}
import it.amongsus.model.actor.ModelActorMessages.{KillTimerStatusModel, MyCharMovedModel, MyPlayerLeftModel}
import it.amongsus.model.actor.ModelActorMessages.{PlayerLeftModel, PlayerMovedModel, RestartGameModel}
import it.amongsus.model.actor.ModelActorMessages.UiActionModel
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ModelActor {
  def props(state: ModelActorInfo): Props =
    Props(new ModelActor(state))
}

class ModelActor(state: ModelActorInfo) extends Actor  with ActorLogging{
  override def receive: Receive = gameBehaviour(state)

  private def gameBehaviour(state: ModelActorInfo): Receive = {
    case InitModel(map: Iterator[String], players: Seq[Player]) =>
      state.gamePlayers = players
      val gameMap = generateMap(map)
      state.controllerRef.get ! ModelReadyController(gameMap, state.myCharacter, players,
        generateCoins(gameMap))
      state.checkTimer(TimerStarted)
      context >>> gameBehaviour(ModelActorInfo(state.controllerRef,
        Option(gameMap), players, generateCoins(gameMap), state.clientId))

    case MyCharMovedModel(direction: Direction) => state.updateMyChar(direction)

    case PlayerMovedModel(player: Player, deadBodiss: Seq[DeadBody]) =>
      state.deadBodies = deadBodiss
      state.updatePlayer(player)
      state.controllerRef.get ! UpdatedPlayersController(state.myCharacter,state.gamePlayers,
        state.gameCoins, state.deadBodies)

    case UiActionModel(action: ActionType) => action match {
      case VentAction => state.useVent()
      case EmergencyAction => state.checkTimer(TimerEnded)
        state.callEmergency()
        state.controllerRef.get ! BeginVotingController(state.gamePlayers)
        context >>> voteBehaviour(state)
      case KillAction => state.kill()
      case ReportAction => state.checkTimer(TimerEnded)
        state.controllerRef.get ! BeginVotingController(state.gamePlayers)
        context >>> voteBehaviour(state)
      case SabotageAction => state.sabotage(true)
        ActorSystemManager.actorSystem.scheduler.scheduleOnce(5 seconds){
          state.sabotage(false)
        }
    }

    case KillTimerStatusModel(status: TimerStatus) => status match {
      case TimerStarted =>
        state.controllerRef.get ! ActionOffController(KillAction)
        state.isTimerOn = true
      case TimerEnded => state.isTimerOn = false
    }
      state.updatePlayer(state.myCharacter)

    case BeginVotingModel => state.checkTimer(TimerEnded)
      context >>> voteBehaviour(state)

    case GameEndModel(end: GameEnd) => state.checkTimer(TimerEnded)
      state.controllerRef.get ! GameEndController(end)
      self ! PoisonPill

    case MyPlayerLeftModel => self ! PoisonPill

    case PlayerLeftModel(clientId: String) => state.removePlayer(clientId)

    case _ => log.info("Model Actor -> game error" )
  }

  private def voteBehaviour(state: ModelActorInfo): Receive = {
    case KillPlayerModel(username: String) =>
      state.killAfterVote(username)
      state.deadBodies = Seq()
      state.controllerRef.get ! UpdatedPlayersController(state.myCharacter,state.gamePlayers,
        state.gameCoins, state.deadBodies)

    case RestartGameModel => state.checkTimer(TimerStarted)
      context >>> gameBehaviour(state)

    case GameEndModel(end: GameEnd) => state.checkTimer(TimerEnded)
      state.controllerRef.get ! GameEndController(end)
      self ! PoisonPill

    case MyPlayerLeftModel => self ! PoisonPill

    case _ => log.info("Model Actor -> vote error" )
  }
}