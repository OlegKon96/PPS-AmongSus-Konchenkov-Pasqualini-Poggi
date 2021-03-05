package it.amongsus.controller.actor

import akka.actor.ActorRef
import it.amongsus.ActorSystemManager
import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Trait that contains all the callback functions of the messages to be sent to the server
 */
trait LobbyActorInfo {
  /**
   * The ID of the Client
   */
  def clientId: String
  /**
   * The reference of the Game Server
   *
   * @return
   */
  def serverRef: Option[ActorRef]
  /**
   * The reference of the Actor's GUI
   *
   * @return
   */
  def guiRef: Option[ActorRef]
  /**
   * Generates the Server Actor Path
   *
   * @param address address of the server to connect
   * @param port port of the server to connect
   * @return
   */
  def generateServerActorPath(address: String, port: Int): String
  /**
   *
   * @param actorPath the path of the actor
   * @return
   */
  def resolveRemoteActorPath(actorPath: String): Future[ActorRef]
}

object LobbyActorInfo {
  def apply(guiRef: Option[ActorRef]): LobbyActorInfoData = LobbyActorInfoData(None, guiRef, "")

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