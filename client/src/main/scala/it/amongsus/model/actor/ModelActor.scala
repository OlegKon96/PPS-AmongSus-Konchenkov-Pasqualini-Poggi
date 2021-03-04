package it.amongsus.model.actor

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import it.amongsus.ActorSystemManager
import it.amongsus.controller.ActionTimer.{TimerEnded, TimerStarted}
import it.amongsus.controller.TimerStatus
import it.amongsus.controller.actor.ControllerActorMessages.{BeginVotingController, ButtonOffController, GameEndController, ModelReadyCotroller, UpdatedPlayersController}
import it.amongsus.core.entities.util.ButtonType.{EmergencyButton, KillButton, ReportButton, SabotageButton, VentButton}
import it.amongsus.model.actor.ModelActorMessages.{BeginVotingModel, GameEndModel, InitModel, KillPlayerModel, KillTimerStatusModel, MyCharMovedModel, PlayerMovedModel, RestartGameModel, UiButtonPressedModel}

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
      state.controllerRef.get ! ModelReadyCotroller(gameMap, state.myCharacter, state.gamePlayers,
        state.gameCollectionables)
      state.checkTimer(TimerStarted)
      context become gameBehaviour(ModelActorInfo(state.controllerRef,
        Option(gameMap), players, state.gameCollectionables, state.clientId))

    case MyCharMovedModel(direction) => state.updateMyChar(direction)

    case PlayerMovedModel(player, deadBodys) =>
      state.deadBodys = deadBodys
      state.updatePlayer(player)
      state.controllerRef.get ! UpdatedPlayersController(state.myCharacter,state.gamePlayers,
        state.gameCollectionables, state.deadBodys)

    case UiButtonPressedModel(button) => button match {
      case _: VentButton => state.useVent()
      case _: EmergencyButton => state.checkTimer(TimerEnded)
        state.callEmergency()
        state.controllerRef.get ! BeginVotingController(state.gamePlayers)
        context become voteBehaviour(state)
      case _: KillButton => state.kill()
      case _: ReportButton => state.checkTimer(TimerEnded)
        state.controllerRef.get ! BeginVotingController(state.gamePlayers)
        context become voteBehaviour(state)
      case _: SabotageButton => state.sabotage()
        ActorSystemManager.actorSystem.scheduler.scheduleOnce(5 seconds){
          state.sabotageOff()
        }
    }

    case KillTimerStatusModel(status: TimerStatus) => status match {
      case TimerStarted =>
        state.controllerRef.get ! ButtonOffController(KillButton())
        state.isTimerOn = true
      case TimerEnded => state.isTimerOn = false
    }
      state.updatePlayer(state.myCharacter)


    case BeginVotingModel() => state.checkTimer(TimerEnded)
      context become voteBehaviour(state)

    case GameEndModel(end) => state.checkTimer(TimerEnded)
      state.controllerRef.get ! GameEndController(end)
      self ! PoisonPill

    case _ => println("error model game")
  }

  private def voteBehaviour(state: ModelActorInfo): Receive = {
    case KillPlayerModel(username) =>
      state.killAfterVote(username)
      state.deadBodys = Seq()
      state.controllerRef.get ! UpdatedPlayersController(state.myCharacter,state.gamePlayers,
        state.gameCollectionables, state.deadBodys)
      
    case RestartGameModel() => state.checkTimer(TimerStarted)
      context become gameBehaviour(state)

    case _ => println("ERROR VOTE")
  }
}
