package it.amongsus.core.entities

import it.amongsus.core.entities.util.Point2D

trait Drawable {
  /**
   * Position of the drawable element
   *
   * @return
   */
  def position: Point2D
}