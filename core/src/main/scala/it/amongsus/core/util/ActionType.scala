package it.amongsus.core.util

/**
 * Trait that manages the Actions of the game
 */
trait ActionType

object ActionType {
  /**
   * Tells that an Impostor wants to use a vent
   */
  case object VentAction extends ActionType
  /**
   * Tells that an Impostor wants to kill a Crewmate
   */
  case object KillAction extends ActionType
  /**
   * Tells that Player wants to call an emergency meeting
   */
  case object EmergencyAction extends ActionType
  /**
   * Tells that a Player wants to report a dead body
   */
  case object ReportAction extends ActionType
  /**
   * Tells that an Impostor wants to make a sabotage
   */
  case object SabotageAction extends ActionType
}