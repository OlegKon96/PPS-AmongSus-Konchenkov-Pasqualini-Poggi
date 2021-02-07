package it.amongsus.view.actor

import akka.actor.ActorRef

trait UiActorInfo {
  /** Reference to server actor */
  def clientRef: Option[ActorRef]
}

object UiActorInfo {
  def apply() : UiActorData = UiActorData(None)
  def apply(clientRef: Option[ActorRef]) : UiActorData = UiActorData(clientRef)
}

case class UiActorData(override val clientRef: Option[ActorRef]) extends UiActorInfo