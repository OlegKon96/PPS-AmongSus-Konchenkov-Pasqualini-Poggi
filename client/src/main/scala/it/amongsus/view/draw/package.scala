package it.amongsus.view

import it.amongsus.core.Entity
import it.amongsus.core.map.{Coin, DeadBody, Tile}
import it.amongsus.core.player.Player

import java.awt.Graphics
import java.awt.image.BufferedImage
import javax.imageio.ImageIO

package object draw {

  final val WALL: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/block.png"))
  final val WALL_OFF: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/blockOff.png"))
  final val SPACE: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/space.png"))
  final val FLOOR: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/floor.png"))
  final val FLOOR_OFF: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/floorOff.png"))
  final val VENT : BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/vent.png"))
  final val VENT_OFF : BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/ventOff.png"))
  final val EMERGENCY : BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/emergencyButton.png"))
  final val EMERGENCY_OFF : BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/emergencyButtonOff.png"))
  final val COIN : BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/coin.png"))
  final val DRAWABLE_SCALING : Int = 15
  final val PLAYER_SCALING : Int = 20

  final val getImageAlive: String => BufferedImage =  color =>
    ImageIO.read(getClass.getResourceAsStream(s"/images/playerAlive$color.png"))

  final val getImageGhost: String => BufferedImage =  color =>
    ImageIO.read(getClass.getResourceAsStream(s"/images/playerGhost$color.png"))

  final val getImageDead: String => BufferedImage =  color =>
    ImageIO.read(getClass.getResourceAsStream(s"/images/playerDead$color.png"))

  final val fieldOfViewDistance: (Tile, Player) => Boolean = (tile,player) =>
    tile.position.distance(player.position) < player.fieldOfView

  final val paintTile: (Graphics, Tile, BufferedImage) => Unit = (g, tile, image) =>
    g.drawImage(image, tile.position.y * DRAWABLE_SCALING + 1, tile.position.x * DRAWABLE_SCALING + 1,
      DRAWABLE_SCALING, DRAWABLE_SCALING, null)

  final val paintDeadBody: (Graphics, DeadBody) => Unit = (g, deadBody) =>
    g.drawImage(getImageDead(deadBody.color), deadBody.position.y * DRAWABLE_SCALING + 1,
      deadBody.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)

  final val paintCoin: (Graphics, Coin) => Unit = (g, coin) =>
    g.drawImage(COIN, coin.position.y * DRAWABLE_SCALING + 1,
      coin.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)

  final val paintPlayer: (Graphics, Player, BufferedImage) => Unit = (g, player, image) =>
    g.drawImage(image, player.position.y * DRAWABLE_SCALING - 1, player.position.x * DRAWABLE_SCALING - 1,
      PLAYER_SCALING, PLAYER_SCALING, null)
}

