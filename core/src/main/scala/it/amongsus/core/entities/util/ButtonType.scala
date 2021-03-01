package it.amongsus.core.entities.util

/**
 * Trait that manages the Buttons of the game
 */
trait ButtonType

object ButtonType {
  /**
   * Tells that Impostor wants to vent
   */
  case class VentButton() extends ButtonType
  /**
   * Tells that Impostor wants to kill
   */
  case class KillButton() extends ButtonType
  /**
   * Tells that Player wants to call emergency
   */
  case class EmergencyButton() extends ButtonType
  /**
   * Tells that Player wants to report
   */
  case class ReportButton() extends ButtonType
  /**
   * Tells that Impostor wants to sabotage
   */
  case class SabotageButton() extends ButtonType
}
