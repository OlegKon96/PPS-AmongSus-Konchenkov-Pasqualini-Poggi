package it.amongsus.view.draw

import it.amongsus.core.map.{Boundary, Emergency, Floor, Other, Vent, Wall}
import it.amongsus.core.player.Player

import java.awt.Graphics

trait DrawableTile[T] {
  def drawTile(tile : T,g : Graphics,player : Player)
}

object DrawableTile {

  def drawTile[T: DrawableTile](tile: T, g: Graphics, player : Player): Unit =
    implicitly[DrawableTile[T]].drawTile(tile, g, player)

  implicit object drawWall extends DrawableTile[Wall] {
    override def drawTile(tile: Wall, g: Graphics, player : Player): Unit = {
      if (tile.position.distance(player.position) < player.fieldOfView) {
        g.drawImage(WALL, tile.position.y * DRAWABLE_SCALING + 1, tile.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
      } else {
        g.drawImage(WALL_OFF, tile.position.y * DRAWABLE_SCALING + 1, tile.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
      }
    }
  }

  implicit object drawVent extends DrawableTile[Vent] {
    override def drawTile(tile: Vent, g: Graphics, player : Player): Unit = {
      if (tile.position.distance(player.position) < player.fieldOfView) {
        g.drawImage(VENT, tile.position.y * DRAWABLE_SCALING + 1, tile.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
      } else {
        g.drawImage(VENT_OFF, tile.position.y * DRAWABLE_SCALING + 1, tile.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
      }
    }
  }

  implicit object drawEmergency extends DrawableTile[Emergency] {
    override def drawTile(tile: Emergency, g: Graphics, player : Player): Unit = {
      if (tile.position.distance(player.position) < player.fieldOfView) {
        g.drawImage(EMERGENCY, tile.position.y * DRAWABLE_SCALING + 1, tile.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
      } else {
        g.drawImage(EMERGENCY_OFF, tile.position.y * DRAWABLE_SCALING + 1, tile.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
      }
    }
  }

  implicit object drawFloor extends DrawableTile[Floor] {
    override def drawTile(tile: Floor, g: Graphics, player : Player): Unit = {
      if (tile.position.distance(player.position) < player.fieldOfView) {
        g.drawImage(FLOOR, tile.position.y * DRAWABLE_SCALING + 1, tile.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
      } else {
        g.drawImage(FLOOR_OFF, tile.position.y * DRAWABLE_SCALING + 1, tile.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
      }
    }
  }

  implicit object drawBoundary extends DrawableTile[Boundary] {
    override def drawTile(tile: Boundary, g: Graphics, player : Player): Unit = {
      g.drawImage(SPACE, tile.position.y * DRAWABLE_SCALING + 1, tile.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
    }
  }

  implicit object drawOther extends DrawableTile[Other] {
    override def drawTile(tile: Other, g: Graphics, player : Player): Unit = {
      g.drawImage(SPACE, tile.position.y * DRAWABLE_SCALING + 1, tile.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
    }
  }
}
