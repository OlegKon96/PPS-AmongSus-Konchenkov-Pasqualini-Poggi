package it.amongsus.server.lobby

import akka.actor.ActorRef

trait LobbyManagerInfo {
  var connectedPlayers: Map[String, ActorRef]
}

object LobbyManagerInfo {
  def apply(connectedPlayers: Map[String, ActorRef]): LobbyManagerInfoData = LobbyManagerInfoData(connectedPlayers)
}

case class LobbyManagerInfoData(override var connectedPlayers: Map[String, ActorRef]) extends LobbyManagerInfo {
  this.connectedPlayers = Map()
}