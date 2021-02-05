package it.amongsus

import it.amongsus.controller.{MainController, MainControllerImpl}

/**
  * The class which initialize the whole client application
  */
object AppLauncher extends App {
  val controller: MainController = new MainControllerImpl()
  controller.start()
}