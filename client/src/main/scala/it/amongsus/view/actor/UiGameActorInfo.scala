package it.amongsus.view.actor

import akka.actor.ActorRef
import it.amongsus.core.entities.map.Collectionable
import it.amongsus.core.entities.player.Player
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
  def gameFrame: Option[GameFrame]

  def updatePlayer(players: Seq[Player], collectionables: Seq[Collectionable]): Unit
}

object UiGameActorInfo {
  def apply() : UiGameActorData = UiGameActorData(None, None)
  def apply(clientRef: Option[ActorRef], gameFrame: Option[GameFrame]) : UiGameActorData =
    UiGameActorData(clientRef, gameFrame)
}

case class UiGameActorData(override val clientRef: Option[ActorRef],
                           override val gameFrame: Option[GameFrame]) extends UiGameActorInfo {

  override def updatePlayer(players: Seq[Player],collectionables: Seq[Collectionable]): Unit =
    gameFrame.get.updatePlayers(players,collectionables)
}