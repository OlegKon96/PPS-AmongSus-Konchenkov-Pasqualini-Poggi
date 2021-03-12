package it.amongsus.model.actor

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import it.amongsus.ActorSystemManager
import it.amongsus.RichActor.RichContext
import it.amongsus.controller.ActionTimer.{TimerEnded, TimerStarted}
import it.amongsus.controller.TimerStatus
import it.amongsus.controller.actor.ControllerActorMessages.{BeginVotingController, ButtonOffController}
import it.amongsus.controller.actor.ControllerActorMessages.{GameEndController, ModelReadyController}
import it.amongsus.controller.actor.ControllerActorMessages.UpdatedPlayersController
import it.amongsus.core.util.ActionType.{EmergencyAction, KillAction, ReportAction, SabotageAction, VentAction}
import it.amongsus.model.actor.ModelActorMessages.{BeginVotingModel, GameEndModel, InitModel, KillPlayerModel}
import it.amongsus.model.actor.ModelActorMessages.{KillTimerStatusModel, MyCharMovedModel, MyPlayerLeftModel}
import it.amongsus.model.actor.ModelActorMessages.{PlayerLeftModel, PlayerMovedModel, RestartGameModel}
import it.amongsus.model.actor.ModelActorMessages.UiButtonPressedModel

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object ModelActor {
  def props(state: ModelActorInfo): Props =
    Props(new ModelActor(state))
}

class ModelActor(state: ModelActorInfo) extends Actor  with ActorLogging{
  override def receive: Receive = gameBehaviour(state)

  private def gameBehaviour(state: ModelActorInfo): Receive = {
    case InitModel(map, players) =>
      state.gamePlayers = players
      val gameMap = state.generateMap(map)
      state.generateCollectionables(gameMap)
      state.controllerRef.get ! ModelReadyController(gameMap, state.myCharacter, state.gamePlayers,
        state.gameCollectionables)
      state.checkTimer(TimerStarted)
      context >>> gameBehaviour(ModelActorInfo(state.controllerRef,
        Option(gameMap), players, state.gameCollectionables, state.clientId))

    case MyCharMovedModel(direction) => state.updateMyChar(direction)

    case PlayerMovedModel(player, deadBodys) =>
      state.deadBodies = deadBodys
      state.updatePlayer(player)
      state.controllerRef.get ! UpdatedPlayersController(state.myCharacter,state.gamePlayers,
        state.gameCollectionables, state.deadBodies)

    case UiButtonPressedModel(button) => button match {
      case _: VentAction => state.useVent()
      case _: EmergencyAction => state.checkTimer(TimerEnded)
        state.callEmergency()
        state.controllerRef.get ! BeginVotingController(state.gamePlayers)
        context >>> voteBehaviour(state)
      case _: KillAction => state.kill()
      case _: ReportAction => state.checkTimer(TimerEnded)
        state.controllerRef.get ! BeginVotingController(state.gamePlayers)
        context >>> voteBehaviour(state)
      case _: SabotageAction => state.sabotage(true)
        ActorSystemManager.actorSystem.scheduler.scheduleOnce(5 seconds){
          state.sabotage(false)
        }
    }

    case KillTimerStatusModel(status: TimerStatus) => status match {
      case TimerStarted =>
        state.controllerRef.get ! ButtonOffController(KillAction())
        state.isTimerOn = true
      case TimerEnded => state.isTimerOn = false
    }
      state.updatePlayer(state.myCharacter)

    case BeginVotingModel() => state.checkTimer(TimerEnded)
      context >>> voteBehaviour(state)

    case GameEndModel(end) => state.checkTimer(TimerEnded)
      state.controllerRef.get ! GameEndController(end)
      self ! PoisonPill

    case MyPlayerLeftModel() => self ! PoisonPill

    case PlayerLeftModel(clientId) => state.removePlayer(clientId)

    case _ => println("error model game")
  }

  private def voteBehaviour(state: ModelActorInfo): Receive = {
    case KillPlayerModel(username) =>
      state.killAfterVote(username)
      state.deadBodies = Seq()
      state.controllerRef.get ! UpdatedPlayersController(state.myCharacter,state.gamePlayers,
        state.gameCollectionables, state.deadBodies)

    case RestartGameModel() => state.checkTimer(TimerStarted)
      context >>> gameBehaviour(state)

    case GameEndModel(end) => state.checkTimer(TimerEnded)
      state.controllerRef.get ! GameEndController(end)
      self ! PoisonPill

    case MyPlayerLeftModel() => self ! PoisonPill

    case _ => println("ERROR VOTE")
  }
}