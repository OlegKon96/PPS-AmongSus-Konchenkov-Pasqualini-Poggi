package it.amongsus.core.util

import it.amongsus.core.Drawable
import it.amongsus.core.map.{Coin, DeadBody, Tile}
import it.amongsus.core.player.Player

object PlayerHelper {
  val checkPosition: (Coin, Player) => Boolean = (coin, player) => coin.position == player.position
  val emergencyDistance: (Drawable[Tile], Player) => Int = (tile, player) => player.position.distance(tile.position)
  val reportDistance: (DeadBody, Player) => Int = (body, player) => player.position.distance(body.position)
}
