package it.amongsus.view.actor

import akka.actor.ActorRef
import it.amongsus.view.frame.MenuFrame

trait UiActorInfo {
  /** Reference to server actor */
  def clientRef: Option[ActorRef]
  /** Reference to server actor */
  def frame: Option[MenuFrame]

  def prova(numPlayers: Int): Unit
}

object UiActorInfo {
  def apply() : UiActorData = UiActorData(None, None)
  def apply(clientRef: Option[ActorRef], frame: Option[MenuFrame]) : UiActorData = UiActorData(clientRef, frame)
}

case class UiActorData(override val clientRef: Option[ActorRef],
                       override val frame: Option[MenuFrame]) extends UiActorInfo{

  override def prova(numPlayers: Int): Unit = {
    println("numPlayers:" + numPlayers)
    //frame.get.toLobby() unsafeRunSync
  }
}