package it.amongsus.server.lobby

import akka.actor.ActorRef

/**
 * Trait of the basic information of the lobby
 */
trait LobbyManagerInfo {
  /**
   * Connected players to the lobby
   */
  var connectedPlayers: Map[String, ActorRef]
}

object LobbyManagerInfo {
  def apply(connectedPlayers: Map[String, ActorRef]): LobbyManagerInfoData = LobbyManagerInfoData(connectedPlayers)
}
/**
 * Information of the lobby
 *
 * @param connectedPlayers to the lobby
 */
case class LobbyManagerInfoData(override var connectedPlayers: Map[String, ActorRef]) extends LobbyManagerInfo {
  this.connectedPlayers = Map()
}