package it.amongsus.core

import it.amongsus.core.util.Point2D

trait Drawable[E <: Drawable[E]] {
  self: E =>
  /**
   * Position of the drawable element.
   *
   * @return a point2D that represent a the position of the drawable.
   */
  def position: Point2D
}