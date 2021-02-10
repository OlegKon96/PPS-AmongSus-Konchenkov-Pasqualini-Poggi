package it.amongsus.model

import akka.actor.ActorRef

/**
 * Trait that contains all Callback functions for server responses
 */
trait GameActorInfo {
  /**
   * The ID of the Client
   */
  def clientId: String

  /**
   * The reference of the Game Server
   * @return
   */
  def gameServerRef: Option[ActorRef]

  /**
   * The reference of the Actor's GUI
   * @return
   */
  def guiRef: Option[ActorRef]
}

object GameActorInfo {
  def apply(gameServerRef: Option[ActorRef], guiRef: Option[ActorRef], clientId: String): GameActorInfo =
    GameActorInfoData(gameServerRef,guiRef, clientId)
}

case class GameActorInfoData(override val gameServerRef: Option[ActorRef],
                             override val guiRef: Option[ActorRef],
                             override val clientId: String) extends GameActorInfo {}