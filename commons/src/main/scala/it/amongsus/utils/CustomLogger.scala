package it.amongsus.utils

trait CustomLogger {
  protected def log(message: String): Unit = println(message)
}