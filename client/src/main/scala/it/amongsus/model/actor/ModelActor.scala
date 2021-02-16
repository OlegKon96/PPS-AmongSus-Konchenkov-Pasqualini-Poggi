package it.amongsus.model.actor

import akka.actor.{Actor, ActorLogging, Props}
import it.amongsus.controller.actor.ControllerActorMessages._
import it.amongsus.model.actor.ModelActorMessages.{InitMapModel, InitPlayersModel, MyCharMovedModel, PlayerMovedModel}

object ModelActor {
  def props(state: ModelActorInfo): Props =
    Props(new ModelActor(state))
}

class ModelActor(state: ModelActorInfo) extends Actor  with ActorLogging{
  override def receive: Receive = gameBehaviour(state)

  private def gameBehaviour(state: ModelActorInfo): Receive = {
    case InitMapModel(map) =>
      context become gameBehaviour(ModelActorInfo(state.controllerRef, Option(state.generateMap(map)), state.clientId))

    case InitPlayersModel(players) => {
      state.generatePlayers(players)
      state.controllerRef.get ! ModelReadyCotroller(state.gameMap.get, state.playersList, state.collectionables)
    }

    case MyCharMovedModel(direction) => {
      state.controllerRef.get ! UpdatedMyCharController(state.myCharacter)
    }

    case PlayerMovedModel(player) => {
      state.controllerRef.get ! UpdatedPlayerController(state.myCharacter)
    }

    case _ => println("error model game")
  }

  private def voteBehaviour(state: ModelActorInfo): Receive = {
    case _ => println("error model vote")
  }
}
