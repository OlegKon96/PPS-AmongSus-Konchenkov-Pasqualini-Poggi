package it.amongsus.controller.actor

import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import it.amongsus.controller.ActionTimer.{ActionTimerImpl, TimerEnded, TimerStarted}
import it.amongsus.controller.{ActionTimer, TimerListener, TimerStatus}
import it.amongsus.core.entities.util.ButtonType
import it.amongsus.core.entities.util.ButtonType.{KillButton, SabotageButton}
import it.amongsus.model.actor.ModelActorMessages.KillTimerStatusModel
import it.amongsus.view.actor.UiActorGameMessages.KillTimerUpdateUi

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

  def checkButton(button: ButtonType): Unit

  def manageKillTimer(status: TimerStatus): Unit
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

  override def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromFile("res/gameMap.csv")
    bufferedSource.getLines
  }

  override def checkButton(button: ButtonType): Unit = button match{
    case KillButton() => killTimer.start()
    case _ =>
  }

  override def manageKillTimer(status: TimerStatus): Unit = status match {
    case TimerStarted => killTimer.start()
    case TimerEnded => killTimer.end()
  }

  private def millisToMinutesAndSeconds(millis: Long): (Long, Long) = {
    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
    (minutes, seconds)
  }
}