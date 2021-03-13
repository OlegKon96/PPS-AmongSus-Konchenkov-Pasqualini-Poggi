package it.amongsus.controller

import java.util.{Timer, TimerTask}

/**
 * Trait that contains all the callback functions of the kill timer.
 */
trait ActionTimer {
  /**
   * Method that starts the times.
   */
  def start()
  /**
   * Method that ends the timer.
   */
  def end()
}

/**
 * Trait that manages the listener of the turn timer.
 */
trait TimerListener {
  /**
   *
   */
  def onStart()
  /**
   *
   */
  def onEnd()
  /**
   * Method that manages the tick of the timer.
   *
   * @param millis milliseconds of the tick.
   */
  def onTick(millis: Long)
}

trait TimerStatus

object ActionTimer {
  case object TimerStarted extends TimerStatus
  case object TimerEnded extends TimerStatus
  private final val MILLIS = 1000

  class ActionTimerImpl(duration: Long, listener: TimerListener) extends ActionTimer {
    var timer: Timer = new Timer()
    var timeRemaining: Long = duration * MILLIS

    var tickTask: TimerTask = _
    var endTask: TimerTask = _

    override def start(): Unit = {
      tickTask = new TimerTask {
        override def run(): Unit = {
          timeRemaining = timeRemaining - MILLIS
          listener.onTick(timeRemaining)
        }
      }

      endTask = new TimerTask {
        override def run(): Unit = {
          end()
          listener.onEnd()
        }
      }
      timer.schedule(tickTask, MILLIS, MILLIS)
      timer.schedule(endTask, duration * MILLIS)
      listener.onStart()
    }

    override def end(): Unit = {
      tickTask.cancel()
      endTask.cancel()
      timer.purge()
      timeRemaining = duration * MILLIS
    }
  }
}