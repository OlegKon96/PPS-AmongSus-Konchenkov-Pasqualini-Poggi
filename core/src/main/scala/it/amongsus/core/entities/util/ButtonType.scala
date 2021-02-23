package it.amongsus.core.entities.util

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
}
