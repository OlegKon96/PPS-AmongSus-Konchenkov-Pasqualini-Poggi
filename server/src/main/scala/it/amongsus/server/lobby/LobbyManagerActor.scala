package it.amongsus.server.lobby

import akka.actor.{Actor, ActorLogging, ActorRef, Props, Terminated}
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.messages.LobbyMessagesServer.LobbyError.PrivateLobbyIdNotValid
import it.amongsus.messages.LobbyMessagesServer._
import it.amongsus.server.common.{GamePlayer, IdGenerator}
import it.amongsus.server.game.GameActor
import it.amongsus.server.game.GameActor.GamePlayers

object LobbyManagerActor {
  def props(): Props = Props(new LobbyManagerActor())
}

class LobbyManagerActor extends Actor with IdGenerator with ActorLogging {
  type UserName = String
  type UserId = String
  private val lobbyManger: LobbyManager[GamePlayer] = LobbyManager()
  private val privateLobbyService: PrivateLobbyService = PrivateLobbyService()
  private var connectedPlayers: Map[UserId, ActorRef] = Map()

  override def receive: Receive = {
    case ConnectServer(clientRef) =>
      log.info(s"client $clientRef is asking for a connection")
      val clientId = generateId
      connectedPlayers = connectedPlayers + (clientId -> clientRef)
      context.watch(clientRef)
      clientRef ! Connected(clientId)

    case JoinPublicLobbyServer(clientId, username, numberOfPlayers) =>
      log.info(s"client $clientId wants to join a public lobby")
      this.executeOnClientRefPresent(clientId) { ref =>
        val lobbyType = PlayerNumberLobby(numberOfPlayers)
        this.lobbyManger.addPlayer(GamePlayer(clientId, username, ref), lobbyType)
        ref ! UserAddedToLobbyClient(this.lobbyManger.getLobby(lobbyType).get.players.length,numberOfPlayers)
        this.lobbyManger.getLobby(lobbyType).get.players.filter(player => player.id != clientId).foreach(player =>
          player.actorRef ! UpdateLobbyClient(this.lobbyManger.getLobby(lobbyType).get.players.length))
        this.checkAndCreateGame(lobbyType)
      }

    case CreatePrivateLobbyServer(clientId, username, numberOfPlayers) =>
      this.executeOnClientRefPresent(clientId) { ref =>
        val lobbyType = privateLobbyService.generateNewPrivateLobby(numberOfPlayers)
        this.lobbyManger.addPlayer(GamePlayer(clientId, username, ref), lobbyType)
        ref ! PrivateLobbyCreatedClient(lobbyType.lobbyId,numberOfPlayers)
      }

    case JoinPrivateLobbyServer(clientId, username, lobbyCode) =>
      this.executeOnClientRefPresent(clientId) { ref =>
        privateLobbyService.retrieveExistingLobby(lobbyCode) match {
          case Some(lobbyType) =>
            val player = GamePlayer(clientId, username, ref)
            this.lobbyManger.addPlayer(player, lobbyType)
            ref ! UserAddedToLobbyClient(this.lobbyManger.getLobby(lobbyType).get.players.length,
              lobbyType.numberOfPlayers)
            this.lobbyManger.getLobby(lobbyType).get.players.filter(player => player.id != clientId).foreach(player =>
              player.actorRef ! UpdateLobbyClient(this.lobbyManger.getLobby(lobbyType).get.players.length))
            this.checkAndCreateGame(lobbyType)
          case None => ref ! LobbyErrorOccurred(PrivateLobbyIdNotValid)
        }
      }

    case LeaveLobbyServer(userId) =>
      log.info(s"client $userId")
      val lobby = lobbyManger.getLobbyPlayer(userId).get
      this.lobbyManger.removePlayer(userId)
      lobby.removePlayer(userId)
        .players.foreach(player => player.actorRef ! UpdateLobbyClient(lobby.players.length - 1))

    case Terminated(actorRef) =>
      log.info(s"terminated $actorRef, connected players: $connectedPlayers")
      removeClient(actorRef)
  }

  private def checkAndCreateGame(lobbyType: LobbyType): Unit = {
    this.lobbyManger.attemptExtractPlayerForMatch(lobbyType) match {
      case Some(players) => this.generateAndStartGameActor(lobbyType)(players)
      case None =>
    }
  }

  private def generateAndStartGameActor(lobbyType: LobbyType)(players: Seq[GamePlayer]): Unit = {
    val gameActor = context.actorOf(GameActor.props(lobbyType.numberOfPlayers))
    players.foreach(p => {
      context.unwatch(p.actorRef)
      // remove player form lobby
      this.lobbyManger.removePlayer(p.id)
      // remove player from connected players structure
      this.connectedPlayers = this.connectedPlayers - p.id
    })
    gameActor ! GamePlayers(players)
  }

  private def executeOnClientRefPresent(clientId: String)(action: ActorRef => Unit): Unit = {
    this.getClientRef(clientId) match {
      case Some(ref) => action(ref)
      case _ =>
    }
  }

  private def getClientRef(clientId: String): Option[ActorRef] = {
    this.connectedPlayers.get(clientId)
  }

  private def removeClient(actorRef: ActorRef): Unit = {
    this.connectedPlayers.find(_._2 == actorRef) match {
      case Some((userId, _)) =>
        log.info(s"removing client $actorRef from lobby and connected players list")
        context.unwatch(actorRef)
        this.lobbyManger.removePlayer(userId)
        this.connectedPlayers = this.connectedPlayers - userId
        log.info(s"removed client $actorRef from lobby and connected players list")
      case None => log.info(s"client $actorRef not found")
    }
  }
}