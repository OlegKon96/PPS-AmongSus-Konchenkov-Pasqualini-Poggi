package it.amongsus.view

import it.amongsus.core.player.Player
import it.amongsus.view.draw.DrawableEntity.getClass

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

  def getImageAlive(color : String) : BufferedImage =  {
    ImageIO.read(getClass.getResourceAsStream(s"/images/playerAlive$color.png"))
  }

  def getImageGhost(color : String) : BufferedImage =  {
    ImageIO.read(getClass.getResourceAsStream(s"/images/playerGhost$color.png"))
  }

  def getImageDead(color : String) : BufferedImage =  {
    ImageIO.read(getClass.getResourceAsStream(s"/images/playerDead$color.png"))
  }
}
