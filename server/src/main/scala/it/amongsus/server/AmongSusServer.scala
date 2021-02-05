package it.amongsus.server

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory
import it.amongsus.server.lobby.LobbyManagerActor

object AmongSusServer extends App {
  val config = ConfigFactory.parseString(
    s"""
       |akka.remote.netty.tcp.hostname = localhost
       |akka.remote.netty.tcp.port = 5150
       |""".stripMargin).withFallback(ConfigFactory.load())

  val system = ActorSystem("AmongSusServer", config)
  system.actorOf(LobbyManagerActor.props(), "lobby")
}


