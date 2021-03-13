package it.amongsus.view.actor

import akka.actor.ActorRef
import it.amongsus.core.map.{Coin, DeadBody}
import it.amongsus.core.player.{Crewmate, Impostor, Player}
import it.amongsus.core.util.{ActionType, GameEnd}
import it.amongsus.view.frame.{GameFrame, WinFrame}

/**
 * Trait that manages the Actor of the game
 */
trait UiGameActorInfo {
  /**
   * The reference of the server actor
   *
   * @return
   */
  def clientRef: Option[ActorRef]
  /**
   * The frame of the game
   *
   * @return
   */
  def gameFrame: Option[GameFrame]
  /**
   *  Method to manage enable of buttons
   *
   * @param button to enable or disable
   * @param boolean that tells is the button is to turn on or off
   */
  def setButtonState(button : ActionType, boolean: Boolean): Unit
  /**
   * Method that updates a character
   *
   * @param myChar to update
   * @param players of the game
   * @param coins of the game
   * @param deadBodies of the game
   */
  def updatePlayer(myChar: Player, players: Seq[Player], coins : Seq[Coin],
                   deadBodies : Seq[DeadBody]): Unit
  /**
   * Method to updates the kill button of the GUI
   *
   * @param seconds to wait to allow click on the button
   */
  def updateKillButton(seconds : Long) : Unit
  /**
   * Method to updates the sabotage button of the GUI
   *
   * @param seconds to wait to allow click on the button
   */
  def updateSabotageButton(seconds : Long) : Unit
  /**
   * Method that manages the end of the game
   *
   * @param myChar in the game
   * @param gameEnd the end info of the game
   */
  def endGame(myChar: Player, gameEnd: GameEnd): Unit
}

object UiGameActorInfo {
  def apply() : UiGameActorData = UiGameActorData(None, None)
  def apply(clientRef: Option[ActorRef], gameFrame: Option[GameFrame]) : UiGameActorData =
    UiGameActorData(clientRef, gameFrame)
}

case class UiGameActorData(override val clientRef: Option[ActorRef],
                           override val gameFrame: Option[GameFrame]) extends UiGameActorInfo {

  override def updatePlayer(myChar: Player, players: Seq[Player], coins : Seq[Coin],
                            deadBodies : Seq[DeadBody]): Unit =
    gameFrame.get.updatePlayers(myChar, players, coins, deadBodies)

  override def setButtonState(action: ActionType, boolean: Boolean): Unit =
    gameFrame.get.setButtonState(action, boolean).unsafeRunSync()

  override def updateKillButton(seconds: Long): Unit = gameFrame.get.updateKillButton(seconds).unsafeRunSync()

  override def updateSabotageButton(seconds: Long): Unit = gameFrame.get.updateSabotageButton(seconds).unsafeRunSync()

  override def endGame(myChar: Player, gameEnd: GameEnd): Unit = {
    myChar match {
      case _: Crewmate => WinFrame(gameEnd).start().unsafeRunSync()
      case _: Impostor => WinFrame(gameEnd).start().unsafeRunSync()
    }
  }
}