package it.amongsus.controller.actor

import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import it.amongsus.Constants
import it.amongsus.controller.ActionTimer.{ActionTimerImpl, TimerEnded, TimerStarted}
import it.amongsus.controller.{ActionTimer, TimerListener, TimerStatus}
import it.amongsus.core.entities.util.ButtonType.{KillButton, SabotageButton}
import it.amongsus.core.entities.util.ButtonType
import it.amongsus.model.actor.ModelActorMessages.KillTimerStatusModel
import it.amongsus.view.actor.UiActorGameMessages.{ButtonOffUi, ButtonOnUi, KillTimerUpdateUi, SabotageTimerUpdateUi}


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
  def lobbyServerRef: Option[ActorRef]
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
  /**
   * The reference to the Model Actor
   *
   * @return
   */
  def modelRef: Option[ActorRef]
  /**
   * Method that loads the map of the game
   *
   * @return
   */
  def loadMap(): Iterator[String]

  def checkButton(button: ButtonType): Unit

  def manageKillTimer(status: TimerStatus): Unit
}

object GameActorInfo {
  def apply(gameServerRef: Option[ActorRef], lobbyServerRef: Option[ActorRef], guiRef: Option[ActorRef],
            modelRef: Option[ActorRef], clientId: String): GameActorInfo =
    GameActorInfoData(gameServerRef, lobbyServerRef, guiRef,modelRef, clientId)
}

case class GameActorInfoData(override val gameServerRef: Option[ActorRef],
                             override val lobbyServerRef: Option[ActorRef],
                             override val guiRef: Option[ActorRef],
                             override val modelRef: Option[ActorRef],
                             override val clientId: String) extends GameActorInfo {

  val killDuration = 10
  val killTimer: ActionTimer = new ActionTimerImpl(killDuration, new TimerListener {

    override def onStart(): Unit = {
      modelRef.get ! KillTimerStatusModel(TimerStarted)
    }

    override def onEnd(): Unit = {
      modelRef.get ! KillTimerStatusModel(TimerEnded)
    }

    override def onTick(millis: Long): Unit = {
      val time = millisToMinutesAndSeconds(millis)
      guiRef.get ! KillTimerUpdateUi(time._1, time._2)
      if(time._2 == killDuration) killTimer.end()
    }
  })
  val sabotageDuration = 15
  val sabotageTimer: ActionTimer = new ActionTimerImpl(sabotageDuration, new TimerListener {

    override def onStart(): Unit = {
      guiRef.get ! ButtonOffUi(SabotageButton())
    }

    override def onEnd(): Unit = {
      guiRef.get ! ButtonOnUi(SabotageButton())
    }

    override def onTick(millis: Long): Unit = {
      val time = millisToMinutesAndSeconds(millis)
      guiRef.get ! SabotageTimerUpdateUi(time._1, time._2)
      if(time._2 == sabotageDuration) sabotageTimer.end()
    }
  })

  override def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/images/gameMap.csv"))
    bufferedSource.getLines
  }

  private def millisToMinutesAndSeconds(millis: Long): (Long, Long) = {
    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
    (minutes, seconds)
  }

  override def checkButton(button: ButtonType): Unit = button match{
    case KillButton() => killTimer.start()
    case SabotageButton() => sabotageTimer.start()
    case _ =>
  }

  override def manageKillTimer(status: TimerStatus): Unit = status match {
    case TimerStarted => killTimer.start(); sabotageTimer.start()
    case TimerEnded => killTimer.end(); sabotageTimer.end()
  }
}