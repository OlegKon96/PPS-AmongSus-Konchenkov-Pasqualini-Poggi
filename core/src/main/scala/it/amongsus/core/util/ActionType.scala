package it.amongsus.core.util

/**
 * Trait that manages the Buttons of the game
 */
trait ActionType

object ActionType {
  /**
   * Tells that Player wants to move up
   */
  case object VentAction extends ActionType
  /**
   * Tells that Player wants to move down
   */
  case object KillAction extends ActionType
  /**
   * Tells that Player wants to move left
   */
  case object EmergencyAction extends ActionType
  /**
   * Tells that Player wants to move right
   */
  case object ReportAction extends ActionType
  /**
   * Tells that Impostor wants to sabotage Crewmate
   */
  case object SabotageAction extends ActionType
}