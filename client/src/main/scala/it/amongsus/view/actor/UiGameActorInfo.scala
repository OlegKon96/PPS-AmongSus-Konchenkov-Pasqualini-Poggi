package it.amongsus.view.actor

import akka.actor.ActorRef
import it.amongsus.view.frame.{GameFrame, MenuFrame}

/**
 *
 */
trait UiGameActorInfo {
  /**
   * The reference of the server actor
   *
   * @return
   */
  def clientRef: Option[ActorRef]

  /**
   *
   *
   * @return
   */
  def menuFrame: Option[MenuFrame]

  /**
   *
   *
   * @return
   */
  def gameFrame: Option[GameFrame]
}

object UiGameActorInfo {
  def apply() : UiGameActorData = UiGameActorData(None, None,None)
  def apply(clientRef: Option[ActorRef], menuFrame: Option[MenuFrame], gameFrame: Option[GameFrame]) : UiGameActorData =
    UiGameActorData(clientRef, menuFrame, gameFrame)
}

case class UiGameActorData(override val clientRef: Option[ActorRef], override val menuFrame: Option[MenuFrame],
                          override val gameFrame: Option[GameFrame]) extends UiGameActorInfo {

}