package it.amongsus.core.map

import it.amongsus.core.Entity
import it.amongsus.core.util.Point2D

/**
 * Trait of the coin of the game that the player should collect
 */
trait Coin extends Entity[Coin]

object Coin{
  def apply(position: Point2D): Coin = CoinImpl("yellow", position)

  private case class CoinImpl(override val color: String, override val position: Point2D) extends Coin
}