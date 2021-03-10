package it.amongsus.core

trait Entity[E <: Entity[E]] extends Drawable[Entity[E]] {
  /**
   * Color of the Entity element
   *
   * @return
   */
  def color: String
}