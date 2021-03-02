package it.amongsus.core.entities.util

/**
 * Trait that manages the End of the game
 */
trait GameEnd

/**
 * Trait that manages the Winning of a Crew
 */
trait WinnerCrew

object GameEnd{
  case class Win() extends GameEnd

  case class Lost() extends GameEnd

  case class ImpostorCrew() extends WinnerCrew

  case class CrewmateCrew() extends WinnerCrew
}