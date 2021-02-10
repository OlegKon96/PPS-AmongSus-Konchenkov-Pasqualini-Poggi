package it.amongsus.view.actor

import akka.actor.ActorRef
import it.amongsus.view.frame.MenuFrame

/**
 * Trait that contains all Callback functions of UiActor
 */
trait UiActorInfo {
  /**
   * The reference of the server actor
   * @return
   */
  def clientRef: Option[ActorRef]

  /**
   *
   * @return
   */
  def frame: Option[MenuFrame]

  /**
   *
   * @param numPlayers the number of the players
   */
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