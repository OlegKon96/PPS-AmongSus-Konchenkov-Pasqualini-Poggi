package it.amongsus.controller

import it.amongsus.ActorSystemManager
import it.amongsus.client.model.lobby.LobbyActor
import it.amongsus.messages.LobbyMessagesClient.{ConnectClient, JoinPublicLobbyClient}
import it.amongsus.model.LobbyActorInfo

class MainControllerImpl() extends MainController {

  private lazy val lobbyActorRef =
    ActorSystemManager.actorSystem.actorOf(LobbyActor.props(LobbyActorInfo()), "client-lobby")
  //val view: ViewImpl = new ViewImpl(Option(lobbyActorRef))

  override def start(): Unit = {
    connect()
    //view.test() unsafeRunSync
  }

  private def connect(): Unit = {
    this.lobbyActorRef.tell(ConnectClient("localhost", 5150), lobbyActorRef)
    Thread sleep 10000
    this.lobbyActorRef.tell(JoinPublicLobbyClient("test", 2), lobbyActorRef)
  }
}