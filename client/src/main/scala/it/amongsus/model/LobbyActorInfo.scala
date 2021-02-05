package it.amongsus.model

import akka.actor.ActorRef
import it.amongsus.client.ActorSystemManager

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Callback functions for server responses
 */
trait LobbyActorInfo {

  /** client id */
  def clientId: String

  /** Reference to server actor */
  def serverRef: Option[ActorRef]

  /** Reference to gui actor */
  def guiRef: Option[ActorRef]

  def generateServerActorPath(address: String, port: Int): String

  def resolveRemoteActorPath(actorPath: String): Future[ActorRef]
}

object LobbyActorInfo {
  def apply(): LobbyActorInfoData = LobbyActorInfoData(None, None, "")

  def apply(serverRef: Option[ActorRef], guiRef: Option[ActorRef], clientId: String): LobbyActorInfoData =
    LobbyActorInfoData(serverRef, guiRef, clientId)
}

case class LobbyActorInfoData(override val serverRef: Option[ActorRef],
                              override val guiRef: Option[ActorRef],
                              override val clientId: String) extends LobbyActorInfo {


  override def generateServerActorPath(address: String, port: Int): String =
    s"akka.tcp://AmongSusServer@$address:$port/user/lobby"

  /**
   * Obtains the ActorRef of the corresponding remote actor
   */
  override def resolveRemoteActorPath(actorPath: String): Future[ActorRef] = {
    ActorSystemManager.actorSystem.actorSelection(actorPath).resolveOne()(10.seconds)
  }
}