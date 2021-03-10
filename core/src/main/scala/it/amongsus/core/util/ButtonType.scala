package it.amongsus.core.util

/**
 * Trait that manages the Buttons of the game
 */
trait ButtonType

object ButtonType {
  /**
   * Tells that Player wants to move up
   */
  case class VentButton() extends ButtonType
  /**
   * Tells that Player wants to move down
   */
  case class KillButton() extends ButtonType
  /**
   * Tells that Player wants to move left
   */
  case class EmergencyButton() extends ButtonType
  /**
   * Tells that Player wants to move right
   */
  case class ReportButton() extends ButtonType
  /**
   * Tells that Impostor wants to sabotage Crewmate
   */
  case class SabotageButton() extends ButtonType
}
