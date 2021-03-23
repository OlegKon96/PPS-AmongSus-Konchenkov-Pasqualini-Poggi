package it.amongsus.controller.actor

import java.util.concurrent.TimeUnit
import akka.actor.ActorRef
import it.amongsus.Constants.Client.GAME_MAP
import it.amongsus.controller.ActionTimer.{ActionTimerImpl, TimerEnded, TimerStarted}
import it.amongsus.controller.{ActionTimer, TimerListener, TimerStatus}
import it.amongsus.core.util.ActionType.{KillAction, SabotageAction}
import it.amongsus.core.util.ActionType
import it.amongsus.model.actor.ModelActorMessages.KillTimerStatusModel
import it.amongsus.view.actor.UiActorGameMessages.{ActionOffUi, ActionOnUi, KillTimerUpdateUi, SabotageTimerUpdateUi}

/**
 * Trait that contains all the callback functions of the messages to be sent to the server.
 */
trait GameActorInfo {
  /**
   * The ID of the Client.
   */
  def clientId: String
  /**
   * The reference of the Game Server.
   *
   * @return game server actor ref.
   */
  def gameServerRef: Option[ActorRef]
  /**
   * The reference of the Actor's GUI.
   *
   * @return ui actor ref.
   */
  def guiRef: Option[ActorRef]
  /**
   * The reference to the Model Actor.
   *
   * @return model actor ref.
   */
  def modelRef: Option[ActorRef]
  /**
   * Method that loads the map of the game.
   *
   * @return string iterator.
   */
  def loadMap(): Iterator[String]
  /**
   * Method that check che button.
   *
   * @param action to check.
   */
  def checkAction(action: ActionType): Unit
  /**
   * Method that manages the Kill Timer.
   *
   * @param status of the timer.
   */
  def manageKillTimer(status: TimerStatus): Unit
}

object GameActorInfo {
  def apply(gameServerRef: Option[ActorRef], guiRef: Option[ActorRef],
            modelRef: Option[ActorRef], clientId: String): GameActorInfo =
    GameActorInfoData(gameServerRef, guiRef,modelRef, clientId)
}

case class GameActorInfoData(override val gameServerRef: Option[ActorRef],
                             override val guiRef: Option[ActorRef],
                             override val modelRef: Option[ActorRef],
                             override val clientId: String) extends GameActorInfo {

  final private val KILL_DURATION = 10
  final private val SABOTAGE_DURATION = 15

  val killTimer: ActionTimer = new ActionTimerImpl(KILL_DURATION, new TimerListener {

    override def onStart(): Unit = {
      modelRef.get ! KillTimerStatusModel(TimerStarted)
    }

    override def onEnd(): Unit = {
      modelRef.get ! KillTimerStatusModel(TimerEnded)
    }

    override def onTick(millis: Long): Unit = {
      val time = millisToMinutesAndSeconds(millis)
      guiRef.get ! KillTimerUpdateUi(time._1, time._2)
      if(time._2 == KILL_DURATION) killTimer.end()
    }
  })

  val sabotageTimer: ActionTimer = new ActionTimerImpl(SABOTAGE_DURATION, new TimerListener {

    override def onStart(): Unit = {
      guiRef.get ! ActionOffUi(SabotageAction)
    }

    override def onEnd(): Unit = {
      guiRef.get ! ActionOnUi(SabotageAction)
    }

    override def onTick(millis: Long): Unit = {
      val time = millisToMinutesAndSeconds(millis)
      guiRef.get ! SabotageTimerUpdateUi(time._1, time._2)
      if(time._2 == SABOTAGE_DURATION) sabotageTimer.end()
    }
  })

  override def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream(GAME_MAP))
    bufferedSource.getLines
  }

  private def millisToMinutesAndSeconds(millis: Long): (Long, Long) = {
    val minutes: Long = TimeUnit.MILLISECONDS.toMinutes(millis)
    val seconds: Long = TimeUnit.MILLISECONDS.toSeconds(millis) - TimeUnit.MINUTES.toSeconds(minutes)
    (minutes, seconds)
  }

  override def checkAction(action: ActionType): Unit = action match{
    case KillAction => killTimer.start()
    case SabotageAction => sabotageTimer.start()
    case _ =>
  }

  override def manageKillTimer(status: TimerStatus): Unit = status match {
    case TimerStarted => killTimer.start(); sabotageTimer.start()
    case TimerEnded => killTimer.end(); sabotageTimer.end()
  }
}