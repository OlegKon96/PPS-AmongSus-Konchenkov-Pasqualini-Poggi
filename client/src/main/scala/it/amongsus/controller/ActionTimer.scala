package it.amongsus.controller

import java.util.{Timer, TimerTask}

/**
 * Trait that contains all the callback functions of the kill timer
 */
trait ActionTimer {
  /**
   * Method that starts the times
   */
  def start()
  /**
   * Method that ends the timer
   */
  def end()
}

/**
 * Trait that manages the listener of the turn timer
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
   * Method that manages the tick of the timer
   *
   * @param millis milliseconds of the tick
   */
  def onTick(millis: Long)
}

trait TimerStatus

object ActionTimer {
  case object TimerStarted extends TimerStatus
  case object TimerEnded extends TimerStatus

  class ActionTimerImpl(duration: Long, listener: TimerListener) extends ActionTimer {
    var timer: Timer = new Timer()
    var timeRemaining: Long = duration * 1000

    var tickTask: TimerTask = _
    var endTask: TimerTask = _

    override def start(): Unit = {
      tickTask = new TimerTask {
        override def run(): Unit = {
          timeRemaining = timeRemaining - 1000
          listener.onTick(timeRemaining)
        }
      }

      endTask = new TimerTask {
        override def run(): Unit = {
          end()
          listener.onEnd()
        }
      }
      timer.schedule(tickTask, 1000, 1000)
      timer.schedule(endTask, duration * 1000)
      listener.onStart()
    }

    override def end(): Unit = {
      tickTask.cancel()
      endTask.cancel()
      timer.purge()
      timeRemaining = duration * 1000
    }
  }
}