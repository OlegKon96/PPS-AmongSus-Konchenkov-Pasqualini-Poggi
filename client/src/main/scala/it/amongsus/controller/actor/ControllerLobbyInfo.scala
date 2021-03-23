package it.amongsus.controller.actor

import akka.actor.ActorRef
import it.amongsus.Constants.Remote.{SERVER_ACTOR_SYSTEM_NAME, SERVER_LOBBY_ACTOR_NAME}
import it.amongsus.{ActorSystemManager, Constants}

import scala.concurrent.Future
import scala.concurrent.duration._

/**
 * Trait that contains all the callback functions of the messages to be sent to the server
 */
trait ControllerLobbyInfo {
  /**
   * The ID of the Client.
   */
  def clientId: String
  /**
   * The reference of the Game Server.
   *
   * @return game server ref.
   */
  def serverRef: Option[ActorRef]
  /**
   * The reference of the Actor's GUI.
   *
   * @return Ui actor ref.
   */
  def guiRef: Option[ActorRef]
  /**
   * Generates the Server Actor Path.
   *
   * @param address address of the server to connect.
   * @param port port of the server to connect.
   * @return connection string.
   */
  def generateServerActorPath(address: String, port: Int): String
  /**
   * Obtains the ActorRef of the corresponding remote actor
   *
   * @param actorPath the path of the actor.
   * @return future contains an ActorRef.
   */
  def resolveRemoteActorPath(actorPath: String): Future[ActorRef]
}

object ControllerLobbyInfo {
  def apply(guiRef: Option[ActorRef]): ControllerLobbyInfoData = ControllerLobbyInfoData(None, guiRef, "")

  def apply(serverRef: Option[ActorRef], guiRef: Option[ActorRef], clientId: String): ControllerLobbyInfoData =
    ControllerLobbyInfoData(serverRef, guiRef, clientId)
}

case class ControllerLobbyInfoData(override val serverRef: Option[ActorRef],
                                   override val guiRef: Option[ActorRef],
                                   override val clientId: String) extends ControllerLobbyInfo {

  override def generateServerActorPath(address: String, port: Int): String =
    s"akka.tcp://${SERVER_ACTOR_SYSTEM_NAME}" +
      s"@$address:$port/user/${SERVER_LOBBY_ACTOR_NAME}"

  override def resolveRemoteActorPath(actorPath: String): Future[ActorRef] = {
    ActorSystemManager.actorSystem.actorSelection(actorPath).resolveOne()(10 seconds)
  }
}