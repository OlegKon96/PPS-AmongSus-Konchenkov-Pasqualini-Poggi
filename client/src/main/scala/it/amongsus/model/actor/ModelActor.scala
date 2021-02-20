package it.amongsus.model.actor

import akka.actor.{Actor, ActorLogging, Props}
import it.amongsus.controller.actor.ControllerActorMessages._
import it.amongsus.model.actor.ModelActorMessages.{InitModel, MyCharMovedModel, PlayerMovedModel}

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
      context become gameBehaviour(ModelActorInfo(state.controllerRef,
        Option(gameMap), players, state.gameCollectionables, state.clientId))

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
