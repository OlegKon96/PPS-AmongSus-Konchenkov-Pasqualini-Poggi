package it.amongsus.server.game

import akka.actor.{Actor, ActorLogging, PoisonPill, Props, Stash, Terminated}
import it.amongsus.core.entities.player.{Constants, CrewmateAlive, ImpostorAlive, Player}
import it.amongsus.core.entities.util.Point2D
import it.amongsus.messages.GameMessageClient.{GamePlayersClient, PlayerMovedClient}
import it.amongsus.messages.GameMessageServer._
import it.amongsus.messages.LobbyMessagesServer._
import it.amongsus.server.common.GamePlayer
import it.amongsus.server.game.GameMatchActor.GamePlayers

import scala.concurrent.duration.DurationInt
import scala.util.Random


object GameMatchActor {
  def props(numberOfPlayers: Int): Props = Props(new GameMatchActor(numberOfPlayers))

  /**
   * Sent to the gameactor to specify the players to add to the match
   *
   * @param players players to add to the match
   */
  case class GamePlayers(players: Seq[GamePlayer])

}

/**
 * Responsible for a game match
 *
 */
class GameMatchActor(numberOfPlayers: Int) extends Actor with ActorLogging with Stash {

  private var players: Seq[GamePlayer] = _

  import context.dispatcher

  override def receive: Receive = idle

  private def idle: Receive = {
    case GamePlayers(players) => {
      log.info(s"initial players $players")
      this.players = players
      this.players.foreach(p => context.watch(p.actorRef))
      require(players.size == numberOfPlayers)
      this.broadcastMessageToPlayers(MatchFound(self))
      context.become(initializing(Seq.empty) orElse terminationBeforeGameStarted())
    }
  }

  /**
   * Waits all players to start game
   *
   * @param playersReady players ready for the game
   */
  private def initializing(playersReady: Seq[GamePlayer]): Receive = {
    case PlayerReadyServer(id, ref) => {
      this.withPlayer(id) { p =>
        log.info(s"player ${p.username} ready")

        val updatedReadyPlayers = playersReady :+ p.copy(actorRef = ref)

        if (updatedReadyPlayers.length == numberOfPlayers) {
          log.info("All players ready")
          this.initializeGame(updatedReadyPlayers)
        } else {
          context.become(initializing(updatedReadyPlayers) orElse terminationBeforeGameStarted())
        }
      }
    }
  }

  /**
   * Inizialize the game, creating the initial state, the turn manager and changing actor behaviour
   *
   */
  private def inGame(): Receive = {
    case PlayerMovedServer(player, deadBodys) =>
      players.filter(p => p.actorRef != sender()).foreach(p => p.actorRef ! PlayerMovedClient(player, deadBodys))
  }

  /**
   * Listen for termination messages before the match start:
   * before all the players sent the Ready message
   */
  private def terminationBeforeGameStarted(): Receive = {
    case Terminated(ref) => {
      this.players.find(_.actorRef == ref) match {
        case Some(player) => {
          log.info(s"player ${player.username} terminated before the game starts")
          // become in behaviour in cui a ogni ready che mi arriva invio il messaggio di fine partita
          // dopo x secondi mi uccido
          broadcastMessageToPlayers(PlayerLeftServer)
          context.become(gameEndedWithErrorBeforeStarts())
          context.system.scheduler.scheduleOnce(20.second) {
            log.info("Terminating game actor..")
            self ! PoisonPill
          }
        }
        case None => log.error(s"client with ref $ref not found")
      }
    }
  }

  /**
   * Listen for termination messages after the match start
   */
  private def terminationAfterGameStarted(): Receive = {
    case Terminated(ref) => this.players.find(_.actorRef == ref) match {
      case Some(player) =>
        log.info(s"Player ${player.username} left the game")
        this.broadcastMessageToPlayers(PlayerLeftServer)
        self ! PoisonPill
    }
    case LeaveGameServer(playerId) => withPlayer(playerId) { player =>
      log.info(s"Player ${player.username} left the game")
      this.broadcastMessageToPlayers(PlayerLeftServer)
      self ! PoisonPill
    }
  }

  /**
   * notify termination to next player if one of them terminates during the game loading
   */
  private def gameEndedWithErrorBeforeStarts(): Receive = {
    case PlayerReadyServer(_, ref) => ref ! PlayerLeftServer
  }

  /**
   * Inizialize the game, creating the initial state, the turn manager and changing actor behaviour
   *
   * @param playersReady all the players ready
   */
  private def initializeGame(playersReady: Seq[GamePlayer]): Unit = {
    // unwatch the player with the old actor ref
    this.players.foreach(p => context.unwatch(p.actorRef))

    this.players = playersReady
    log.debug(s"ready players $playersReady")
    log.debug(s"updated players $players")

    // watch the players with the new actor ref
    val playersRole = defineRoles()
    this.players.foreach(p => {
      p.actorRef ! GamePlayersClient(playersRole)
      context.watch(p.actorRef)
    })
    context.become(inGame() orElse terminationAfterGameStarted())
  }

  private def defineRoles(): Seq[Player] = {
    var playersRole: Seq[Player] = Seq()
    val rand1 = Random.nextInt(players.length)
    val colors = Random.shuffle(Seq("green", "red", "cyan", "yellow", "blue", "pink", "orange"))
    val rand2 = if(players.length > 5) Random.nextInt(players.length) else rand1
    val mapCentre = Point2D(35,35)

    for (n <- players.indices) {
      n match {
        case n if n == rand1 || n == rand2 =>
          playersRole = playersRole :+ ImpostorAlive(colors(n), emergencyCalled = false, players(n).id,
            players(n).username, mapCentre)
        case _ => playersRole = playersRole :+ CrewmateAlive(colors(n), emergencyCalled = false, players(n).id,
          players(n).username, Constants.Crewmate.NUM_COINS, mapCentre)
      }
    }
    playersRole
  }

  /**
   * Broadcast a generic message to all game players
   *
   * @param message a generic message
   */
  private def broadcastMessageToPlayers(message: Any): Unit = {
    this.players.foreach(p => p.actorRef ! message)
  }


  /**
   * Send and update message about the game state to each player
   *
   */
  private def broadcastGameStateToPlayers() {

  }

  private def withPlayer(playerId: String)(f: GamePlayer => Unit): Unit = {
    this.players.find(_.id == playerId) match {
      case Some(p) => f(p)
      case None => log.info(s"Player id $playerId not found")
    }
  }

}
