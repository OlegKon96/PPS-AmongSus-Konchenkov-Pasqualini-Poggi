package it.amongsus.view.panel

import it.amongsus.core.entities.map.{Boundary, Collectionable, DeadBody, Emergency,
  Floor, Other, Tile, Vent, Wall}
import it.amongsus.core.entities.player.{AlivePlayer, CrewmateAlive, CrewmateGhost,
  DeadPlayer, ImpostorAlive, ImpostorGhost, Player}
import java.awt.Graphics
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel

trait GamePanel extends JPanel{
  def updateGame(myChar: Player,
                 players: Seq[Player],
                 collectionables : Seq[Collectionable],
                 deadBodies : Seq[DeadBody]) : Unit
}

object GamePanel {
  def apply(map: Array[Array[Tile]],
            myChar: Player,
            players: Seq[Player],
            collectionables: Seq[Collectionable],
            deadBodies: Seq[DeadBody]): GamePanel = new GamePanelImpl(map, myChar, players, collectionables, deadBodies)

  private class GamePanelImpl(map: Array[Array[Tile]],
                              myChar: Player,
                              players: Seq[Player],
                              collectionables: Seq[Collectionable],
                              deadBodies: Seq[DeadBody]) extends GamePanel {

    private val gameMap = map
    private var gamePlayers = players
    private var gameCollectionables = collectionables
    private var gameDeadBodies = deadBodies
    private var gameMyChar = myChar

    val block: BufferedImage = ImageIO.read(new File("res/block.png"))
    val blockOff: BufferedImage = ImageIO.read(new File("res/blockOff.png"))
    val space: BufferedImage = ImageIO.read(new File("res/space.png"))
    val floor: BufferedImage = ImageIO.read(new File("res/floor.png"))
    val floorOff: BufferedImage = ImageIO.read(new File("res/floorOff.png"))
    val vent: BufferedImage = ImageIO.read(new File("res/vent.png"))
    val ventOff : BufferedImage = ImageIO.read(new File("res/ventOff.png"))
    val emergency: BufferedImage = ImageIO.read(new File("res/emergencyButton.png"))
    val emergencyOff : BufferedImage = ImageIO.read(new File("res/emergencyButtonOff.png"))
    val coin: BufferedImage = ImageIO.read(new File("res/coin.png"))
    val SCALEFACTOR: Int = 15
    val ONE : Int = 1
    val ZERO : Int = 0
    val WIDTH : Int = 1080
    val HEIGHT : Int = 750

    override def paintComponent(g: Graphics): Unit = {
      g.clearRect(ZERO, ZERO, WIDTH, HEIGHT)
      drawMap(g)
      drawEntities(g)
    }

    private def drawMap(g: Graphics): Unit = {
      gameMyChar match {
        case _: AlivePlayer =>
          gameMap.foreach(x => x.foreach {
            case tile: Vent => if (tile.position.distance(gameMyChar.position) < gameMyChar.fieldOfView) {
                g.drawImage(vent,
                  tile.position.y * SCALEFACTOR + ONE,
                  tile.position.x * SCALEFACTOR + ONE,
                  SCALEFACTOR, SCALEFACTOR, this)
              } else { g.drawImage(ventOff,
                  tile.position.y * SCALEFACTOR + ONE,
                  tile.position.x * SCALEFACTOR + ONE,
                  SCALEFACTOR, SCALEFACTOR, this)
              }
            case tile: Emergency => if (tile.position.distance(gameMyChar.position) < gameMyChar.fieldOfView) {
              g.drawImage(emergency,
                  tile.position.y * SCALEFACTOR + ONE,
                  tile.position.x * SCALEFACTOR + ONE,
                  SCALEFACTOR, SCALEFACTOR, this)
              } else { g.drawImage(emergencyOff,
                  tile.position.y * SCALEFACTOR + ONE,
                  tile.position.x * SCALEFACTOR + ONE,
                  SCALEFACTOR, SCALEFACTOR, this)
              }
            case tile: Boundary => g.drawImage(space,
                tile.position.y * SCALEFACTOR + ONE,
                tile.position.x * SCALEFACTOR + ONE,
                SCALEFACTOR, SCALEFACTOR, this)
            case tile: Wall =>
              if (tile.position.distance(gameMyChar.position) < gameMyChar.fieldOfView) {
                g.drawImage(block,
                  tile.position.y * SCALEFACTOR + ONE,
                  tile.position.x * SCALEFACTOR + ONE,
                  SCALEFACTOR, SCALEFACTOR, this)
              } else { g.drawImage(blockOff,
                  tile.position.y * SCALEFACTOR + ONE,
                  tile.position.x * SCALEFACTOR + ONE,
                  SCALEFACTOR, SCALEFACTOR, this)
              }
            case tile: Floor => if (tile.position.distance(gameMyChar.position) < gameMyChar.fieldOfView) {
                g.drawImage(floor,
                  tile.position.y * SCALEFACTOR + ONE,
                  tile.position.x * SCALEFACTOR + ONE,
                  SCALEFACTOR, SCALEFACTOR, this)
              } else { g.drawImage(floorOff,
                  tile.position.y * SCALEFACTOR + ONE,
                  tile.position.x * SCALEFACTOR + ONE,
                  SCALEFACTOR, SCALEFACTOR, this)
              }
            case tile: Other => g.drawImage(space,
                tile.position.y * SCALEFACTOR + ONE,
                tile.position.x * SCALEFACTOR + ONE,
                SCALEFACTOR, SCALEFACTOR, this)
          })
        case _: DeadPlayer => gameMap.foreach(x => x.foreach {
            case tile: Vent => g.drawImage(vent,
              tile.position.y * SCALEFACTOR + ONE,
              tile.position.x * SCALEFACTOR + ONE,
                SCALEFACTOR, SCALEFACTOR, this)
            case tile: Emergency => g.drawImage(emergency,
              tile.position.y * SCALEFACTOR + ONE,
              tile.position.x * SCALEFACTOR + ONE,
              SCALEFACTOR, SCALEFACTOR, this)
            case tile: Boundary => g.drawImage(space,
              tile.position.y * SCALEFACTOR + ONE,
              tile.position.x * SCALEFACTOR + ONE,
              SCALEFACTOR, SCALEFACTOR, this)
            case tile: Wall => g.drawImage(block,
              tile.position.y * SCALEFACTOR + ONE,
              tile.position.x * SCALEFACTOR + ONE,
              SCALEFACTOR, SCALEFACTOR, this)
            case tile: Floor => g.drawImage(floor,
              tile.position.y * SCALEFACTOR + ONE,
              tile.position.x * SCALEFACTOR + ONE,
              SCALEFACTOR, SCALEFACTOR, this)
            case tile: Other => g.drawImage(space,
              tile.position.y * SCALEFACTOR + ONE,
              tile.position.x * SCALEFACTOR + ONE,
              SCALEFACTOR, SCALEFACTOR, this)
          })
      }
    }

    private def drawEntities(g: Graphics): Unit = {
      gameMyChar match {
        case _: ImpostorAlive =>
          gameDeadBodies.filter(body => body.position.distance(gameMyChar.position) < gameMyChar.fieldOfView)
            .foreach(deadBody => g.drawImage(getImageDead(deadBody.color),
              deadBody.position.y * SCALEFACTOR + ONE,
              deadBody.position.x * SCALEFACTOR + ONE,
              SCALEFACTOR, SCALEFACTOR, this))
          gamePlayers.filter(player => player.position.distance(gameMyChar.position) < gameMyChar.fieldOfView)
            .foreach {
            case impostorAlive: ImpostorAlive => drawImpostor(g, impostorAlive, getImageAlive(impostorAlive.color))
            case crewmateAlive: CrewmateAlive => drawCrewmate(g, crewmateAlive, getImageAlive(crewmateAlive.color))
            case _ =>
          }
        case _: ImpostorGhost =>
          gameDeadBodies.foreach(deadBody => g.drawImage(getImageDead(deadBody.color),
            deadBody.position.y * SCALEFACTOR + ONE,
            deadBody.position.x * SCALEFACTOR + ONE,
            SCALEFACTOR, SCALEFACTOR, this))
          gamePlayers.foreach {
            case impostorAlive: ImpostorAlive => drawImpostor(g, impostorAlive, getImageAlive(impostorAlive.color))
            case impostorGhost: ImpostorGhost => drawImpostor(g, impostorGhost, getImageGhost(impostorGhost.color))
            case crewmateAlive: CrewmateAlive => drawCrewmate(g, crewmateAlive, getImageAlive(crewmateAlive.color))
            case crewmateGhost: CrewmateGhost => drawCrewmate(g, crewmateGhost, getImageGhost(crewmateGhost.color))
          }
        case _: CrewmateAlive =>
          gameDeadBodies.filter(body => body.position.distance(gameMyChar.position) < gameMyChar.fieldOfView)
            .foreach(deadBody => g.drawImage(getImageDead(deadBody.color),
              deadBody.position.y * SCALEFACTOR + ONE,
              deadBody.position.x * SCALEFACTOR + ONE,
              SCALEFACTOR, SCALEFACTOR, this))
          gameCollectionables.filter(collectionable =>
            collectionable.position.distance(gameMyChar.position) < gameMyChar.fieldOfView)
            .foreach(collectionable => g.drawImage(coin,
              collectionable.position.y * SCALEFACTOR + ONE,
              collectionable.position.x * SCALEFACTOR + ONE,
              SCALEFACTOR, SCALEFACTOR, this))
          gamePlayers.filter(player => player.position.distance(gameMyChar.position) < gameMyChar.fieldOfView)
            .foreach {
            case alivePlayer: AlivePlayer => drawCrewmate(g, alivePlayer, getImageAlive(alivePlayer.color))
            case _ =>
          }
        case _: CrewmateGhost =>
          gameDeadBodies.foreach(deadBody => g.drawImage(getImageDead(deadBody.color),
            deadBody.position.y * SCALEFACTOR + ONE,
            deadBody.position.x * SCALEFACTOR + ONE,
            SCALEFACTOR, SCALEFACTOR, this))
          gameCollectionables.foreach(collectionable => g.drawImage(coin,
            collectionable.position.y * SCALEFACTOR + ONE,
            collectionable.position.x * SCALEFACTOR + ONE,
            SCALEFACTOR, SCALEFACTOR, this))
          gamePlayers.foreach {
            case ap: AlivePlayer => drawCrewmate(g, ap, getImageAlive(ap.color))
            case dp: DeadPlayer => drawCrewmate(g, dp, getImageGhost(dp.color))
          }

      }
    }

    private def getImageAlive(color: String): BufferedImage = {
      ImageIO.read(new File(s"res/playerAlive$color.png"))
    }

    private def getImageGhost(color: String): BufferedImage = {
      ImageIO.read(new File(s"res/playerGhost$color.png"))
    }

    private def getImageDead(color: String): BufferedImage = {
      ImageIO.read(new File(s"res/playerDead$color.png"))
    }

    private def drawImpostor(g: Graphics, player: Player, image: BufferedImage): Unit = {
      g.drawImage(image,
        player.position.y * SCALEFACTOR + ONE,
        player.position.x * SCALEFACTOR + ONE,
        20, 20, null)
      g.drawString(player.username.toUpperCase,
        player.position.y * SCALEFACTOR - 10,
        player.position.x * SCALEFACTOR - 5)
    }

    private def drawCrewmate(g: Graphics, player: Player, image: BufferedImage): Unit = {
      g.drawImage(image,
        player.position.y * SCALEFACTOR + ONE,
        player.position.x * SCALEFACTOR + ONE,
        20, 20, null)
      g.drawString(player.username.toLowerCase(),
        player.position.y * SCALEFACTOR - 10,
        player.position.x * SCALEFACTOR - 5)
    }

    def updateGame(myChar: Player,
                   players: Seq[Player],
                   collectionables: Seq[Collectionable],
                   deadBodies: Seq[DeadBody]): Unit = {
      this.gameMyChar = myChar
      this.gamePlayers = players
      this.gameCollectionables = collectionables
      this.gameDeadBodies = deadBodies
      repaint()
    }
  }

}
