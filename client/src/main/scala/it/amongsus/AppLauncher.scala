package it.amongsus

import it.amongsus.controller.{MainController, MainControllerImpl}

/**
 * Class that initializes the whole client application
 */
object AppLauncher extends App {
  val controller: MainController = new MainControllerImpl()
  controller.start()
}