package it.amongsus.view.actor

import akka.actor.ActorRef
import it.amongsus.view.frame.{Frame, LobbyFrame, MenuFrame}

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
   * The current frame
   *
   * @return
   */
  def currentFrame: Option[Frame]
  /**
   * Update the label of players in LobbyFrame
   *
   * @param numPlayers the numbers of the players
   */
  def updateLobby(numPlayers: Int): Unit
  /**
   * Saves the code of the lobby
   *
   * @param lobbyCode the code of the lobby
   */
  def saveCode(lobbyCode: String): Unit
  /**
   * Returns lobby code if it exist
   *
   * @return lobby code
   */
  def getCode: String
  /**
   * Notify an error occurred
   */
  def lobbyError(): Unit
  /**
   * Method to show start button in the GUI
   */
  def showStartButton(): Unit
}

object UiActorInfo {
  def apply(): UiActorData = UiActorData(None, None)

  def apply(clientRef: Option[ActorRef], currentFrame: Option[Frame]): UiActorData =
    UiActorData(clientRef, currentFrame)
}

case class UiActorData(override val clientRef: Option[ActorRef],
                       override val currentFrame: Option[Frame]) extends UiActorInfo {

  override def updateLobby(numPlayers: Int): Unit =
    currentFrame.get.asInstanceOf[LobbyFrame].updatePlayers(numPlayers).unsafeRunSync()

  override def saveCode(lobbyCode: String): Unit = {
    currentFrame.get.asInstanceOf[MenuFrame].saveCode(lobbyCode)
  }

  override def lobbyError(): Unit = {
    currentFrame.get.asInstanceOf[MenuFrame].lobbyError()
  }

  override def getCode: String = currentFrame.get.asInstanceOf[MenuFrame].code

  override def showStartButton(): Unit = currentFrame.get.asInstanceOf[LobbyFrame].showButton(true).unsafeRunSync()
}