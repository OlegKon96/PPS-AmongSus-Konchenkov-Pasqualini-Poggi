package it.amongsus.core.player

import it.amongsus.core.Drawable
import it.amongsus.core.map.{Coin, DeadBody, Tile}

object PlayerHelper {
  val checkPosition: (Coin, Player) => Boolean = (coin, player) => coin.position == player.position
  val emergencyDistance: (Drawable[Tile], Player) => Int = (tile, player) => player.position.distance(tile.position)
  val reportDistance: (DeadBody, Player) => Int = (body, player) => player.position.distance(body.position)
}