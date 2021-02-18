package it.amongsus.core.entities.util

trait ButtonType

object ButtonType {
  /**
   * Tells that Player wants to vent
   */
  case class VentButton() extends ButtonType
  /**
   * Tells that Player wants to kill
   */
  case class KillButton() extends ButtonType
  /**
   * Tells that Player wants to call emergency
   */
  case class EmergencyButton() extends ButtonType
  /**
   * Tells that Player wants to move report
   */
  case class ReportButton() extends ButtonType
}
