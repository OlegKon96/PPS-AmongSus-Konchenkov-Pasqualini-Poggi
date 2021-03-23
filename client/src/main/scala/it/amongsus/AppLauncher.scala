package it.amongsus

import it.amongsus.controller.{Controller, ControllerImpl}

/**
 * Class that initializes the whole client application
 */
object AppLauncher extends App {
  val controller: Controller = new ControllerImpl()
  controller.start()
}