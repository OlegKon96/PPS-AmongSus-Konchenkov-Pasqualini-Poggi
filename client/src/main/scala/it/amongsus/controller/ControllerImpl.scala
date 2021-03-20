package it.amongsus.controller

import it.amongsus.Constants.Remote.{SERVER_ADDRESS, SERVER_PORT}
import it.amongsus.{ActorSystemManager, Constants}
import it.amongsus.messages.LobbyMessagesClient.ConnectClient
import it.amongsus.controller.actor.{ControllerActor, LobbyActorInfo}
import it.amongsus.view.actor.{UiActor, UiActorInfo}

/**
 * Class of the Controller that manages the Client Actor and the Server Actor.
 */
class ControllerImpl() extends Controller {
  private lazy val guiRef =
    ActorSystemManager.actorSystem.actorOf(UiActor.props(UiActorInfo()), "gui")
  private lazy val lobbyActorRef =
    ActorSystemManager.actorSystem.actorOf(ControllerActor.props(LobbyActorInfo(Option(guiRef))), "client-lobby")

  override def start(): Unit = {
    connect()
  }

  private def connect(): Unit = {
    this.lobbyActorRef.tell(ConnectClient(SERVER_ADDRESS, SERVER_PORT), lobbyActorRef)
  }
}