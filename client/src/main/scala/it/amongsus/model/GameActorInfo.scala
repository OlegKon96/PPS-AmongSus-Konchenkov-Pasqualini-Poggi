package it.amongsus.model

import akka.actor.ActorRef

/**
 * Callback functions for server responses
 */
trait GameActorInfo {
  /** client id */
  def clientId: String

  def gameServerRef: Option[ActorRef]

  /** Reference to gui actor */
  def guiRef: Option[ActorRef]
}

object GameActorInfo {
  def apply(gameServerRef: Option[ActorRef], guiRef: Option[ActorRef], clientId: String): GameActorInfo =
    GameActorInfoData(gameServerRef,guiRef, clientId)
}

case class GameActorInfoData(override val gameServerRef: Option[ActorRef],
                             override val guiRef: Option[ActorRef],
                             override val clientId: String) extends GameActorInfo {}