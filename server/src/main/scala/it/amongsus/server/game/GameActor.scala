package it.amongsus.server.game

import akka.actor.{Actor, ActorLogging, PoisonPill, Props, Stash, Terminated}
import it.amongsus.RichActor.RichContext
import it.amongsus.core.player._
import it.amongsus.core.util.GameEnd.{CrewmateCrew, ImpostorCrew}
import it.amongsus.core.util.ChatMessage
import it.amongsus.messages.GameMessageClient.{EliminatedPlayer, GamePlayersClient}
import it.amongsus.messages.GameMessageClient.{NoOneEliminatedController, PlayerLeftClient, PlayerMovedClient}
import it.amongsus.messages.GameMessageClient.{SendTextChatClient, StartVotingClient, VoteClient}
import it.amongsus.messages.GameMessageServer._
import it.amongsus.messages.LobbyMessagesServer._
import it.amongsus.server.common.GamePlayer
import it.amongsus.server.game.GameActor.GamePlayers
import scala.concurrent.duration.DurationInt

object GameActor {
  def props(state: GameActorInfo): Props = Props(new GameActor(state))
  /**
   * Sent to the Game Actor to specify the players to add to the match
   *
   * @param players players to add to the match
   */
  case class GamePlayers(players: Seq[GamePlayer])
}

/**
 * Responsible of game match
 *
 * @param state information of the actor
 */
class GameActor(private val state: GameActorInfo) extends Actor with ActorLogging with Stash {
  import context.dispatcher

  override def receive: Receive = idle

  private def idle: Receive = {
    case GamePlayers(players: Seq[Player]) =>
      log.info(s"Server -> initial players $players")
      this.state.players = players
      this.state.players.foreach(p => context.watch(p.actorRef))
      require(players.size == this.state.numberOfPlayers)
      this.state.broadcastMessageToPlayers(MatchFound(self))
      context >>> (initializing(Seq.empty) orElse terminationBeforeGameStarted())
  }

  /**
   * Waits all players to start game
   *
   * @param playersReady players ready for the game
   */
  private def initializing(playersReady: Seq[GamePlayer]): Receive = {
    case PlayerReadyServer(id, ref) =>
      this.state.withPlayer(id) { p =>
        log.info(s"Server -> player ${p.username} ready")
        val updatedReadyPlayers = playersReady :+ p.copy(actorRef = ref)
        if (updatedReadyPlayers.length == this.state.numberOfPlayers) {
          log.info("Server -> All players ready")
          this.initializeGame(updatedReadyPlayers)
        } else {
          context >>> (initializing(updatedReadyPlayers) orElse terminationBeforeGameStarted())
        }
      }
  }

  /**
   * Initialize the game, creating the initial state, the turn manager and changing actor behaviour
   */
  private def inGame(): Receive = {
    case PlayerMovedServer(player, gamePlayers, deadBodys) =>
      if(this.state.checkWinCrewmate(gamePlayers)){
        this.state.sendWinMessage(gamePlayers, CrewmateCrew(), self)
      } else if(this.state.checkWinImpostor(gamePlayers)){
        this.state.sendWinMessage(gamePlayers, ImpostorCrew(), self)
      } else{
        this.state.players.filter(p => p.actorRef != sender()).foreach(p =>
          p.actorRef ! PlayerMovedClient(player, deadBodys))
      }

    case StartVoting(gamePlayers: Seq[Player]) =>
      this.state.totalVotes = gamePlayers.count(p => p.isInstanceOf[AlivePlayer])
      this.state.players.filter(p => p.actorRef != sender()).foreach(p => p.actorRef ! StartVotingClient(gamePlayers))
      context >>> voting(gamePlayers)
  }

  private def voting(gamePlayers: Seq[Player]): Receive = {
    case VoteClient(username: String) => manageVote(username, gamePlayers)

    case SendTextChatServer(message: ChatMessage, character: Player) => character match {
      case _: DeadPlayer => for{
        deadPlayer <- gamePlayers.filter{ case _:DeadPlayer => true case _ => false}
        player <- state.players.filter(player => deadPlayer.clientId == player.id && player.actorRef != sender())
      } player.actorRef ! SendTextChatClient(message)

      case _: AlivePlayer => for{
        alivePlayer <- gamePlayers.filter{ case _:AlivePlayer => true case _ => false}
        player <- state.players.filter(player => alivePlayer.clientId == player.id && player.actorRef != sender())
      } player.actorRef ! SendTextChatClient(message)
    }

    case _ => log.info("Server -> Game Actor -> voting error...")
  }

  /**
   * Listen for termination messages before the match start: before all the players sent the Ready message
   */
  private def terminationBeforeGameStarted(): Receive = {
    case Terminated(ref) =>
      this.state.players.find(_.actorRef == ref) match {
        case Some(player) =>
          log.info(s"player ${player.username} terminated before the game starts")
          // become in behaviour in cui a ogni ready che mi arriva invio il messaggio di fine partita
          this.state.broadcastMessageToPlayers(PlayerLeftClient(player.id))
          context >>> gameEndedWithErrorBeforeStarts(player.id)
          context.system.scheduler.scheduleOnce(20.second) {
            log.info("Terminating game actor..")
            self ! PoisonPill
          }
        case None => log.error(s"Server -> client with ref $ref not found")
      }
  }

  /**
   * Listen for termination messages after the match start
   */
  private def terminationAfterGameStarted(): Receive = {
    case Terminated(ref) => this.state.players.find(_.actorRef == ref) match {
      case Some(player) =>
        this.state.players = this.state.players.filter(_.actorRef != ref)
        log.info(s"Server -> Player ${player.username} left the game")
        this.state.broadcastMessageToPlayers(PlayerLeftClient(player.id))
    }
    case LeaveGameServer(playerId) => this.state.withPlayer(playerId) { player =>
      this.state.players = this.state.players.filter(_.id != playerId)
      log.info(s"Server -> Player ${player.username} left the game")
      this.state.broadcastMessageToPlayers(PlayerLeftClient(player.id))
    }
  }

  /**
   * Notify termination to next player if one of them terminates during the game loading
   */
  private def gameEndedWithErrorBeforeStarts(clientId: String): Receive = {
    case PlayerReadyServer(_, ref) => ref ! PlayerLeftClient(clientId)
  }

  /**
   * Initialize the game, creating the initial state, the turn manager and changing actor behaviour
   *
   * @param playersReady all the players ready
   */
  private def initializeGame(playersReady: Seq[GamePlayer]): Unit = {
    // unwatch the player with the old actor ref
    this.state.players.foreach(p => context.unwatch(p.actorRef))
    this.state.players = playersReady
    log.debug(s"Server -> ready players $playersReady")
    log.debug(s"Server -> updated players ${this.state.players}")
    // watch the players with the new actor ref
    val playersRole = this.state.defineRoles()
    this.state.players.foreach(p => {
      p.actorRef ! GamePlayersClient(playersRole)
      context.watch(p.actorRef)
    })
    this.state.players.groupBy(_.username).foreach {
      case(username, _) => this.state.playersToLobby = this.state.playersToLobby + (username -> 0)
    }
    context >>> (inGame() orElse terminationAfterGameStarted())
  }

  private def manageVote(username: String, gamePlayer: Seq[Player]): Unit = {
    this.state.totalVotes = this.state.totalVotes - 1
    if(username != "") {
      this.state.playersToLobby = this.state.playersToLobby.updated(username,
        this.state.playersToLobby.find(_._1 == username).get._2 + 1)
    }
    if (this.state.totalVotes <= 0) {
      if (this.state.playersToLobby.count(_._2 == this.state.playersToLobby.valuesIterator.max) > 1) {

        for {
          p <- this.state.players
        } yield p.actorRef ! NoOneEliminatedController

        context >>> inGame()
      } else {
        val playerToEliminate = this.state.playersToLobby.maxBy(_._2)._1

        for {
          p <- this.state.players
        } yield p.actorRef ! EliminatedPlayer(playerToEliminate)

        this.state.playersToLobby = Map.empty

        for {
          p <- gamePlayer
          if p.username != playerToEliminate && p.isInstanceOf[AlivePlayer]
        } yield this.state.playersToLobby = this.state.playersToLobby + (p.username -> 0)

        this.state.totalVotes = this.state.playersToLobby.size

        if (this.state.checkWinCrewmate(gamePlayer.filter(p => p.username != playerToEliminate))) {
          this.state.sendWinMessage(gamePlayer, CrewmateCrew(), self)
          self ! PoisonPill
        } else if (this.state.checkWinImpostor(gamePlayer.filter(p => p.username != playerToEliminate))) {
          this.state.sendWinMessage(gamePlayer, ImpostorCrew(), self)
          self ! PoisonPill
        } else {
          context >>> inGame()
        }
      }
    }
  }
}