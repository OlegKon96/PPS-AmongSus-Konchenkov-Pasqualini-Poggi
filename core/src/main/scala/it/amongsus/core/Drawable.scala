package it.amongsus.core

import it.amongsus.core.util.Point2D

trait Drawable[E <: Drawable[E]] {
  self: E =>
  /**
   * Position of the drawable element
   *
   * @return
   */
  def position: Point2D
}