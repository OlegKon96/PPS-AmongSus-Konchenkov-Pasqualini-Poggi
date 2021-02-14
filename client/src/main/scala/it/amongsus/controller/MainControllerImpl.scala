package it.amongsus.controller

import it.amongsus.model.{LobbyActor, LobbyActorInfo}
import it.amongsus.{ActorSystemManager, Constants}
import it.amongsus.messages.LobbyMessagesClient.ConnectClient
import it.amongsus.view.actor.{UiActor, UiActorInfo}
import it.amongsus.view.frame.MenuFrame

/**
 * Class of the Controller that manages the Client Actor and the Server Actor
 */
class MainControllerImpl() extends MainController {

  private lazy val guiRef =
    ActorSystemManager.actorSystem.actorOf(UiActor.props(UiActorInfo()), "gui")
  private lazy val lobbyActorRef =
    ActorSystemManager.actorSystem.actorOf(LobbyActor.props(LobbyActorInfo(Option(guiRef))), "client-controller")

  override def start(): Unit =
    this.lobbyActorRef.tell(ConnectClient(Constants.Remote.SERVER_ADDRESS, Constants.Remote.SERVER_PORT), lobbyActorRef)
}