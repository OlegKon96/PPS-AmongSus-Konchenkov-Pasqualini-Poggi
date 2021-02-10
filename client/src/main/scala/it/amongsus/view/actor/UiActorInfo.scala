package it.amongsus.view.actor

import akka.actor.ActorRef
import it.amongsus.view.frame.{LobbyFrame, MenuFrame}

/**
 * Trait that contains all Callback functions of UiActor
 */
trait UiActorInfo {
  /**
   * The reference of the server actor
   * @return
   */
  def clientRef: Option[ActorRef]

  def menuFrame: Option[MenuFrame]

  def lobbyFrame: Option[LobbyFrame]

  def toLobby(numPlayers: Int) : Unit

  def toGame(): Unit

  def saveCode(lobbyCode : String) : Unit

  def lobbyError() : Unit
}

object UiActorInfo {
  def apply() : UiActorData = UiActorData(None, None, None)
  def apply(clientRef: Option[ActorRef], menuFrame: Option[MenuFrame],lobbyFrame: Option[LobbyFrame]) : UiActorData = UiActorData(clientRef, menuFrame, lobbyFrame)
}

case class UiActorData(override val clientRef: Option[ActorRef],
                       override val menuFrame: Option[MenuFrame],
                       override val lobbyFrame: Option[LobbyFrame]) extends UiActorInfo{

  override def toLobby(numPlayers: Int): Unit = {
    menuFrame.get.toLobby(numPlayers) unsafeRunSync()
  }

  override def toGame(): Unit = ???

  override def saveCode(lobbyCode: String): Unit = {
    menuFrame.get.saveCode(lobbyCode)
    menuFrame.get.toLobby(1) unsafeRunSync()
  }

  override def lobbyError(): Unit = {
    menuFrame.get.lobbyError()
  }
}