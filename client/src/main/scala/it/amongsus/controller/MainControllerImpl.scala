package it.amongsus.controller

import it.amongsus.model.{LobbyActor, LobbyActorInfo}
import it.amongsus.{ActorSystemManager, Constants}
import it.amongsus.messages.LobbyMessagesClient.ConnectClient
import it.amongsus.view.actor.{UiActor, UiActorInfo}
import it.amongsus.view.frame.MenuFrame

/**
 * Class of the Controller of the Client
 */
class MainControllerImpl() extends MainController {

  private lazy val guiRef =
    ActorSystemManager.actorSystem.actorOf(UiActor.props(UiActorInfo()), "gui")
  private lazy val lobbyActorRef =
    ActorSystemManager.actorSystem.actorOf(LobbyActor.props(LobbyActorInfo(Option(guiRef))), "client-lobby")
  val menuView: MenuFrame  =  MenuFrame(Option(guiRef))

  override def start(): Unit = {
    connect()
    menuView.start() unsafeRunSync()
  }

  private def connect(): Unit =
    this.lobbyActorRef.tell(ConnectClient(Constants.Remote.SERVER_ADDRESS, Constants.Remote.SERVER_PORT), lobbyActorRef)
}