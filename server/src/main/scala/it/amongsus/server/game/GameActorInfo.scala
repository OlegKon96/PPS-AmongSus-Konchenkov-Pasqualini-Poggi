package it.amongsus.server.game

import akka.actor.{ActorRef, PoisonPill}
import it.amongsus.core.player.{AlivePlayer, Constants, Crewmate, CrewmateAlive, Impostor, ImpostorAlive, Player}
import it.amongsus.core.util.GameEnd.{CrewmateCrew, ImpostorCrew, Lost, Win}
import it.amongsus.core.util.{Point2D, WinnerCrew}
import it.amongsus.messages.GameMessageClient.GameEndClient
import it.amongsus.server.common.GamePlayer
import it.amongsus.utils.CustomLogger
import scala.util.Random

/**
 * Trait of the info of Game Actor
 */
trait GameActorInfo {
  /**
   * Sequence of the players of the game
   */
  var players: Seq[GamePlayer]
  /**
   * Total Votes Count of Vote Phase
   */
  var totalVotes: Int
  /**
   * Total Number of Players of the game
   */
  val numberOfPlayers: Int
  /**
   * Map Username-Vote of all players in the lobby
   */
  var playersToLobby: Map[String, Int]
  /**
   *  Method to send a Win Message to all Winners Player
   *
   * @param gamePlayers of the game
   * @param crew of the winner
   * @param gameActor to send win messages
   */
  def sendWinMessage(gamePlayers: Seq[Player], crew: WinnerCrew, gameActor: ActorRef): Unit
  /**
   * Method to define Roles of the players
   *
   * @return
   */
  def defineRoles(): Seq[Player]
  /**
   * Method to send a broadcast message to all players
   *
   * @param message to send
   */
  def broadcastMessageToPlayers(message: Any): Unit
  /**
   * Method to apply function to a player
   *
   * @param playerId of the game
   * @param f function
   */
  def withPlayer(playerId: String)(f: GamePlayer => Unit): Unit
  /**
   * Method to check if all Crewmate have collect all coins
   *
   * @param gamePlayers of the Crewmate in game
   * @return true of false
   */
  def checkWinCrewmate(gamePlayers: Seq[Player]): Boolean
  /**
   * Method to check if all coins are collected
   *
   * @param gamePlayers of the game that should collect the coins
   * @return true of false
   */
  def checkAllCoinsCollected(gamePlayers: Seq[Player]): Boolean
  /**
   * Method to check if impostor/s win the game
   *
   * @param gamePlayers of the game that is/are impostor/s
   * @return true of false
   */
  def checkWinImpostor(gamePlayers: Seq[Player]): Boolean
}

object GameActorInfo {
  def apply(players: Seq[GamePlayer], totalVotes: Int, playersToLobby: Map[String,Int], numberOfPlayers: Int):
    GameActorInfoData = GameActorInfoData(players, totalVotes, playersToLobby, numberOfPlayers)

  def apply(numberOfPlayers: Int): GameActorInfoData = GameActorInfoData(Seq(), numberOfPlayers, Map(), numberOfPlayers)
}

case class GameActorInfoData(override var players: Seq[GamePlayer], override var totalVotes: Int,
                             override var playersToLobby: Map[String,Int], override val numberOfPlayers: Int)
                            extends GameActorInfo with CustomLogger {
  this.totalVotes = this.numberOfPlayers
  this.playersToLobby = Map.empty
  private final val mapCenter: Int = 35
  private final val colorsSequence: Seq[String] = Seq("green", "red", "cyan", "yellow", "blue", "pink", "orange")

  override def sendWinMessage(gamePlayers: Seq[Player], crew: WinnerCrew, gameActor: ActorRef): Unit = {
    gamePlayers.foreach{
      case c : Crewmate =>
        crew match {
          case ImpostorCrew => this.players.find(p => p.id == c.clientId).get.actorRef !
            GameEndClient(Lost(gamePlayers.filter(player => player.isInstanceOf[Impostor]), crew))
          case CrewmateCrew => this.players.find(p => p.id == c.clientId).get.actorRef !
            GameEndClient(Win(gamePlayers.filter(player => player.isInstanceOf[Crewmate]), crew))
        }
      case i : Impostor =>
        crew match {
          case ImpostorCrew => this.players.find(p => p.id == i.clientId).get.actorRef !
            GameEndClient(Win(gamePlayers.filter(player => player.isInstanceOf[Impostor]), crew))
          case CrewmateCrew => this.players.find(p => p.id == i.clientId).get.actorRef !
            GameEndClient(Lost(gamePlayers.filter(player => player.isInstanceOf[Crewmate]), crew))
        }
    }
    log("Game ended...")
    gameActor ! PoisonPill
  }

  override def defineRoles(): Seq[Player] = {
    var playersRole: Seq[Player] = Seq()
    val rand1 = Random.nextInt(this.players.length)
    val colors = Random.shuffle(colorsSequence)
    val rand2 = if(this.players.length > 5) Random.nextInt(this.players.length) else rand1
    val mapCentre = Point2D(mapCenter, mapCenter)

    for (n <- this.players.indices) {
      n match {
        case n if n == rand1 || n == rand2 =>
          playersRole = playersRole :+ ImpostorAlive(colors(n), emergencyCalled = false, this.players(n).id,
            this.players(n).username, mapCentre)
        case _ => playersRole = playersRole :+ CrewmateAlive(colors(n), emergencyCalled = false,
          this.players(n).id, this.players(n).username, Constants.Crewmate.NUM_COINS, mapCentre)
      }
    }
    playersRole
  }

  override def broadcastMessageToPlayers(message: Any): Unit = {
    players.foreach(p => p.actorRef ! message)
  }

  override def withPlayer(playerId: String)(f: GamePlayer => Unit): Unit = {
    players.find(_.id == playerId) match {
      case Some(p) => f(p)
      case None => log(s"Player id $playerId not found")
    }
  }

  override def checkWinCrewmate(gamePlayers: Seq[Player]): Boolean = {
    gamePlayers.count(player => player.isInstanceOf[ImpostorAlive]) == 0 || checkAllCoinsCollected(gamePlayers)
  }

  override def checkAllCoinsCollected(gamePlayers: Seq[Player]): Boolean = {
    var count = 0
    gamePlayers.foreach {
      case p: Crewmate =>
        if (p.numCoins == 10) count += 1
      case _ =>
    }
    if (count == gamePlayers.count(player => player.isInstanceOf[Crewmate])) true else false
  }

  override def checkWinImpostor(gamePlayers: Seq[Player]): Boolean = {
    gamePlayers.count(player =>
      player.isInstanceOf[AlivePlayer]) <= gamePlayers.count(player => player.isInstanceOf[ImpostorAlive]) * 2
  }
}