package it.amongsus.model.actor

import akka.actor.{Actor, ActorLogging, PoisonPill, Props}
import it.amongsus.controller.ActionTimer.{TimerEnded, TimerStarted}
import it.amongsus.controller.TimerStatus
import it.amongsus.controller.actor.ControllerActorMessages.{ButtonOffController, GameEndController, ModelReadyCotroller, UpdatedPlayersController}
import it.amongsus.core.entities.util.ButtonType.{EmergencyButton, KillButton, ReportButton, VentButton}
import it.amongsus.model.actor.ModelActorMessages.{BeginVotingModel, GameEndModel, InitModel, KillTimerStatusModel, MyCharMovedModel, PlayerMovedModel, RestartGameModel, UiButtonPressedModel}

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

    case UiButtonPressedModel(button) =>
      button match {
        case b : VentButton => state.useVent()
        case e : EmergencyButton => state.checkTimer(TimerEnded)
          state.callEmergency()
        case k : KillButton => state.kill()
        case r : ReportButton => state.checkTimer(TimerEnded)
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
    case RestartGameModel() => state.checkTimer(TimerStarted)
      context become gameBehaviour(state)

    case _ => println("ERROR VOTE")
  }
}
