package it.amongsus.server.game

import it.amongsus.server.common.GamePlayer

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
}

object GameActorInfo {
  def apply(players: Seq[GamePlayer], totalVotes: Int, playersToLobby: Map[String,Int], numberOfPlayers: Int):
    GameActorInfoData = GameActorInfoData(players, totalVotes, playersToLobby, numberOfPlayers)

  def apply(numberOfPlayers: Int): GameActorInfoData = GameActorInfoData(Seq(), numberOfPlayers, Map(), numberOfPlayers)
}

case class GameActorInfoData(override var players: Seq[GamePlayer], override var totalVotes: Int,
                             override var playersToLobby: Map[String,Int], override val numberOfPlayers: Int)
                            extends GameActorInfo {
  this.totalVotes = this.numberOfPlayers
  this.playersToLobby = Map.empty
}