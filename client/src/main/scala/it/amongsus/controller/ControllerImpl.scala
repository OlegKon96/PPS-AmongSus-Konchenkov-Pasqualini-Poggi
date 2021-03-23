package it.amongsus.controller

import it.amongsus.Constants.Remote.{SERVER_ADDRESS, SERVER_PORT}
import it.amongsus.{ActorSystemManager}
import it.amongsus.messages.LobbyMessagesClient.ConnectClient
import it.amongsus.controller.actor.{ControllerActor, ControllerLobbyInfo}
import it.amongsus.view.actor.{UiActor, UiLobbyInfo}

/**
 * Class of the Controller that manages the Client Actor and the Server Actor.
 */
class ControllerImpl() extends Controller {
  private lazy val guiRef =
    ActorSystemManager.actorSystem.actorOf(UiActor.props(UiLobbyInfo()), "gui")
  private lazy val lobbyActorRef =
    ActorSystemManager.actorSystem.actorOf(ControllerActor.props(ControllerLobbyInfo(Option(guiRef))), "client-lobby")

  override def start(): Unit = {
    this.lobbyActorRef.tell(ConnectClient(SERVER_ADDRESS, SERVER_PORT), lobbyActorRef)
  }

}