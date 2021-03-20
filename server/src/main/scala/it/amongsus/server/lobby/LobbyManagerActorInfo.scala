package it.amongsus.server.lobby

import akka.actor.ActorRef

/**
 * Trait of the basic information of the lobby
 */
trait LobbyManagerActorInfo {
  /**
   * Connected players to the lobby
   */
  var connectedPlayers: Map[String, ActorRef]
}

object LobbyManagerActorInfo {
  def apply(connectedPlayers: Map[String, ActorRef]): LobbyManagerActorInfoData =
    LobbyManagerActorInfoData(connectedPlayers)
}
/**
 * Information of the lobby
 *
 * @param connectedPlayers to the lobby
 */
case class LobbyManagerActorInfoData(override var connectedPlayers: Map[String, ActorRef])
                                                                                extends LobbyManagerActorInfo {
  this.connectedPlayers = Map()
}