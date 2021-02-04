package it.amongsus.server.lobby

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import it.amongsus.messages.LobbyMessages._
import it.amongsus.server.common.{GamePlayer, IdGenerator}

object LobbyManagerActor {

  def props() = Props(new LobbyManagerActor())

}

class LobbyManagerActor extends Actor with IdGenerator with ActorLogging {

  type UserName = String
  type UserId = String

  private var connectedPlayers: Map[UserId, ActorRef] = Map()
  private val lobbyManger: LobbyManager[GamePlayer] = LobbyManager()

  override def receive: Receive = {
    case Connect(clientRef) => {
      log.info(s"client $clientRef is asking for a connection")
      val clientId = generateId
      connectedPlayers = connectedPlayers + (clientId -> clientRef)
      context.watch(clientRef)
      clientRef ! Connected(clientId)
    }
  }
}