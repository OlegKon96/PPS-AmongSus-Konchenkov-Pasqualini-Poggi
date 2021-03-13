package it.amongsus.core

trait Entity[E <: Entity[E]] extends Drawable[Entity[E]] {
  /**
   * Color of the Entity element.
   *
   * @return the color of the entity.
   */
  def color: String
}