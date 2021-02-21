package it.amongsus.controller.actor

import akka.actor.ActorRef

/**
 * Trait that contains all the callback functions of the messages to be sent to the server
 */
trait GameActorInfo {
  /**
   * The ID of the Client
   */
  def clientId: String
  /**
   * The reference of the Game Server
   *
   * @return
   */
  def gameServerRef: Option[ActorRef]
  /**
   * The reference of the Actor's GUI
   *
   * @return
   */
  def guiRef: Option[ActorRef]

  def modelRef: Option[ActorRef]

  def loadMap(): Iterator[String]
}

object GameActorInfo {
  def apply(gameServerRef: Option[ActorRef], guiRef: Option[ActorRef],
            modelRef: Option[ActorRef], clientId: String): GameActorInfo =
    GameActorInfoData(gameServerRef,guiRef,modelRef, clientId)
}

case class GameActorInfoData(override val gameServerRef: Option[ActorRef],
                             override val guiRef: Option[ActorRef],
                             override val modelRef: Option[ActorRef],
                             override val clientId: String) extends GameActorInfo {

  override def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromFile("res/gameMap.csv")
    bufferedSource.getLines
  }
}