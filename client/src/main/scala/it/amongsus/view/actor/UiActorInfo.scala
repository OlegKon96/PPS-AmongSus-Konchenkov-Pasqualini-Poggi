package it.amongsus.view.actor

import akka.actor.ActorRef
import it.amongsus.view.frame.{LobbyFrame, MenuFrame}

/**
 * Trait that contains all Callback functions of UiActor
 */
trait UiActorInfo {
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
  def lobbyFrame: Option[LobbyFrame]

  /**
   * Open the lobby panel
   *
   * @param numPlayers the numbers of the players
   */
  def toLobby(numPlayers: Int) : Unit

  /**
   * Open the lobby panel
   *
   * @param numPlayers the numbers of the players
   */
  def updateLobby(numPlayers: Int) : Unit

  /**
   * Open the game panel
   */
  def toGame(): Unit

  /**
   * Saves the code of the lobby
   *
   * @param lobbyCode the code of the lobby
   */
  def saveCode(lobbyCode : String) : Unit

  /**
   * Notify an error occurred
   */
  def lobbyError() : Unit
}

object UiActorInfo {
  def apply() : UiActorData = UiActorData(None, None, None)
  def apply(clientRef: Option[ActorRef], menuFrame: Option[MenuFrame],lobbyFrame: Option[LobbyFrame]) : UiActorData =
    UiActorData(clientRef, menuFrame, lobbyFrame)
}

case class UiActorData(override val clientRef: Option[ActorRef],
                       override val menuFrame: Option[MenuFrame],
                       override val lobbyFrame: Option[LobbyFrame]) extends UiActorInfo{

  override def toLobby(numPlayers: Int): Unit = menuFrame.get.toLobby(numPlayers) unsafeRunSync()
  
  override def updateLobby(numPlayers: Int): Unit = lobbyFrame.get.updatePlayers(numPlayers) unsafeRunSync()

  override def toGame(): Unit = {
    lobbyFrame.get.toGame unsafeRunSync()
  }

  override def saveCode(lobbyCode: String): Unit = {
    menuFrame.get.saveCode(lobbyCode)
    menuFrame.get.toLobby(1) unsafeRunSync()
  }

  override def lobbyError(): Unit = {
    menuFrame.get.lobbyError()
  }
}