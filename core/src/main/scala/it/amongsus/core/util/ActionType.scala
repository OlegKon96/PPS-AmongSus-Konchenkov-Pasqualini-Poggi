package it.amongsus.core.util

/**
 * Trait that manages the Buttons of the game
 */
trait ActionType

object ActionType {
  /**
   * Tells that Player wants to move up
   */
  case class VentAction() extends ActionType
  /**
   * Tells that Player wants to move down
   */
  case class KillAction() extends ActionType
  /**
   * Tells that Player wants to move left
   */
  case class EmergencyAction() extends ActionType
  /**
   * Tells that Player wants to move right
   */
  case class ReportAction() extends ActionType
  /**
   * Tells that Impostor wants to sabotage Crewmate
   */
  case class SabotageAction() extends ActionType
}
