package it.amongsus.view.panel

import it.amongsus.core.entities.map.{Boundary, Collectionable, DeadBody, Emergency, Floor, Other, Tile, Vent, Wall}
import it.amongsus.core.entities.player.Player

import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel

trait GamePanel extends JPanel{
  def updateGame(players: Seq[Player],collectionables: Seq[Collectionable]) : Unit

}

object GamePanel {
  def apply(map : Array[Array[Tile]],
            myChar : Player,
            players : Seq[Player],
            collectionables : Seq[Collectionable],
            deadBodies : Seq[DeadBody]) : GamePanel = new GamePanelImpl(map, myChar, players,collectionables, deadBodies)

  private class GamePanelImpl(map : Array[Array[Tile]],
                              myChar : Player,
                              players : Seq[Player],
                              collectionables : Seq[Collectionable],
                              deadBodies : Seq[DeadBody]) extends GamePanel {

    private val gameMap = map
    private var gamePlayers = players
    private var gameCollectionables = collectionables

    val block: BufferedImage = ImageIO.read(new File("res/block.png"))
    val space: BufferedImage = ImageIO.read(new File("res/space.png"))
    val floor: BufferedImage = ImageIO.read(new File("res/floor.png"))
    val vent : BufferedImage = ImageIO.read(new File("res/vent.png"))
    val emergency : BufferedImage = ImageIO.read(new File("res/emergencyButton.png"))
    val playerPic : BufferedImage = ImageIO.read(new File("res/playerAlivegreen.png"))
    val coin : BufferedImage = ImageIO.read(new File("res/coin.png"))

    override def paintComponent(g : Graphics): Unit = {
      g.clearRect(0, 0, 1080, 750)
      drawMap(g)
      drawPlayers(g)
    }

    private def drawMap(g : Graphics) : Unit = {
      gameMap.foreach(x => x.foreach {
        case tile: Vent => g.drawImage(vent, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
        case tile: Emergency => g.drawImage(emergency, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
        case tile: Boundary => g.drawImage(space, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
        case tile: Wall => g.drawImage(block, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
        case tile: Floor => g.drawImage(floor, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, null)
        case tile: Other => g.drawImage(space, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
      })
    }

    private def drawPlayers(g : Graphics) : Unit = {
      gameCollectionables.foreach(collectionable => g.drawImage(coin, collectionable.position.y * 15 + 1, collectionable.position.x * 15 + 1, 15, 15, null))
      gamePlayers.foreach(player => g.drawImage(playerPic, player.position.y * 15 + 1, player.position.x * 15 + 1, 15, 15, null))
    }

    override def updateGame(players: Seq[Player],collectionables: Seq[Collectionable]): Unit = {
      this.gamePlayers = players
      this.gameCollectionables = collectionables
      repaint()
    }
  }
}
