package it.amongsus.server

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import it.amongsus.Constants
import it.amongsus.server.lobby.{LobbyManagerActor, LobbyManagerActorInfo}

object AmongSusServer extends App {
  val config = ConfigFactory.parseString(
    s"""
       |akka.remote.netty.tcp.hostname = ${Constants.Remote.SERVER_ADDRESS}
       |akka.remote.netty.tcp.port = ${Constants.Remote.SERVER_PORT}
       |""".stripMargin).withFallback(ConfigFactory.load())

  val system = ActorSystem(Constants.Remote.SERVER_ACTOR_SYSTEM_NAME, config)
  system.actorOf(LobbyManagerActor.props(LobbyManagerActorInfo(Map())), Constants.Remote.SERVER_LOBBY_ACTOR_NAME)
}