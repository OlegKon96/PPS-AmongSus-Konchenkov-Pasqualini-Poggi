package it.amongsus.view.panel

import it.amongsus.core.entities.map.{Boundary, Collectionable, DeadBody, Emergency, Floor, Other, Tile, Vent, Wall}
import it.amongsus.core.entities.player.{AlivePlayer, CrewmateAlive, CrewmateGhost, DeadPlayer, ImpostorAlive}
import it.amongsus.core.entities.player.{ImpostorGhost, Player}
import it.amongsus.view.frame.GameFrame
import java.awt.Graphics

import javax.swing.JPanel
import javax.imageio.ImageIO
import java.awt.image.BufferedImage

import it.amongsus.core.entities.Drawable

trait GamePanel extends JPanel {
  /**
   * Method that update the game
   *
   * @param myChar of the game
   * @param players of the game
   * @param collectionables of the game
   * @param deadBodies in the game
   */
  def updateGame(myChar: Player, players: Seq[Player], collectionables : Seq[Collectionable],
                 deadBodies : Seq[DeadBody]) : Unit
}

object GamePanel {
  def apply(gameFrame: GameFrame,
            map : Array[Array[Drawable[Tile]]],
            myChar: Player,
            players : Seq[Player],
            collectionables : Seq[Collectionable],
            deadBodies : Seq[DeadBody]): GamePanel =
    new GamePanelImpl(gameFrame, map,myChar, players,collectionables,deadBodies)

  private class GamePanelImpl(gameFrame: GameFrame,
                              map : Array[Array[Drawable[Tile]]],
                              myChar: Player,
                              players : Seq[Player],
                              collectionables : Seq[Collectionable],
                              deadBodies : Seq[DeadBody]) extends GamePanel {

    private var gamePlayers = players
    private var gameCollectionables = collectionables
    private var gameDeadBodies = deadBodies
    private var gameMyChar = myChar
    private val gameMap = map

    val block: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/block.png"))
    val blockOff: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/blockOff.png"))
    val space: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/space.png"))
    val floor: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/floor.png"))
    val floorOff: BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/floorOff.png"))
    val vent : BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/vent.png"))
    val ventOff : BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/ventOff.png"))
    val emergency : BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/emergencyButton.png"))
    val emergencyOff : BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/emergencyButtonOff.png"))
    val coin : BufferedImage = ImageIO.read(getClass.getResourceAsStream("/images/coin.png"))

    override def paintComponent(g : Graphics): Unit = {
      g.clearRect(0, 0, 1080, 750)
      drawMap(g)
      drawEntity(g)
    }

    private def drawMap(g:Graphics) : Unit = {
      gameMyChar match {

        case _: AlivePlayer =>
          gameMap.foreach(x => x.foreach {
            case tile: Vent =>
              if (tile.position.distance(gameMyChar.position) < gameMyChar.fieldOfView) {
                g.drawImage(vent, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
              } else {
                g.drawImage(ventOff, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
              }
            case tile: Emergency =>
              if (tile.position.distance(gameMyChar.position) < gameMyChar.fieldOfView) {
                g.drawImage(emergency, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
              } else {
                g.drawImage(emergencyOff, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
              }
            case tile: Boundary =>
              g.drawImage(space, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
            case tile: Wall =>
              if (tile.position.distance(gameMyChar.position) < gameMyChar.fieldOfView) {
                g.drawImage(block, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
              } else {
                g.drawImage(blockOff, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
              }
            case tile: Floor =>
              if (tile.position.distance(gameMyChar.position) < gameMyChar.fieldOfView) {
                g.drawImage(floor, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, null)
              } else {
                g.drawImage(floorOff, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
              }
            case tile: Other =>
              g.drawImage(space, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
          })

        case _: DeadPlayer =>
          gameMap.foreach(x => x.foreach {
            case tile: Vent => g.drawImage(vent, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
            case tile: Emergency => g.drawImage(emergency, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
            case tile: Boundary => g.drawImage(space, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
            case tile: Wall => g.drawImage(block, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
            case tile: Floor => g.drawImage(floor, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, null)
            case tile: Other => g.drawImage(space, tile.position.y * 15 + 1, tile.position.x * 15 + 1, 15, 15, this)
          })
      }
    }

    private def drawEntity(g: Graphics): Unit ={
      //rendering in base al proprio giocatore
      gameMyChar match {
        case _: ImpostorAlive =>
          gameDeadBodies.filter(body => body.position.distance(gameMyChar.position) < gameMyChar.fieldOfView)
            .foreach(deadBody => g.drawImage(getImageDead(deadBody.color), deadBody.position.y * 15 + 1,
              deadBody.position.x * 15 + 1, 15, 15, null))
          gamePlayers.filter(player => player.position.distance(gameMyChar.position) < gameMyChar.fieldOfView).foreach {
            case impostorAlive: ImpostorAlive =>
              drawImpostor(g, impostorAlive, getImageAlive(impostorAlive.color))
            case crewmateAlive: CrewmateAlive =>
              drawCrewmate(g, crewmateAlive, getImageAlive(crewmateAlive.color))
            case _ =>
          }
        case _: ImpostorGhost =>
          gameDeadBodies.foreach(deadBody => g.drawImage(getImageDead(deadBody.color), deadBody.position.y * 15 + 1,
            deadBody.position.x * 15 + 1, 15, 15, null))
          gamePlayers.foreach {
            case impostorAlive: ImpostorAlive => drawImpostor(g, impostorAlive, getImageAlive(impostorAlive.color))
            case impostorGhost: ImpostorGhost => drawImpostor(g, impostorGhost, getImageGhost(impostorGhost.color))
            case crewmateAlive: CrewmateAlive => drawCrewmate(g, crewmateAlive, getImageAlive(crewmateAlive.color))
            case crewmateGhost: CrewmateGhost => drawCrewmate(g, crewmateGhost, getImageGhost(crewmateGhost.color))
          }

        case _: CrewmateAlive =>
          gameDeadBodies.filter(body => body.position.distance(gameMyChar.position) < gameMyChar.fieldOfView)
            .foreach(deadBody => g.drawImage(getImageDead(deadBody.color), deadBody.position.y * 15 + 1,
              deadBody.position.x * 15 + 1, 15, 15, null))
          gameCollectionables.filter(collectionable => collectionable.position.distance(gameMyChar.position) <
            gameMyChar.fieldOfView).foreach(collectionable => g.drawImage(coin, collectionable.position.y * 15 + 1,
            collectionable.position.x * 15 + 1, 15, 15, null))
          gamePlayers.filter(player => player.position.distance(gameMyChar.position) < gameMyChar.fieldOfView).foreach {
            case alivePlayer: AlivePlayer => drawCrewmate(g, alivePlayer, getImageAlive(alivePlayer.color))
            case _ =>
          }

        case _: CrewmateGhost =>
          gameDeadBodies.foreach(deadBody => g.drawImage(getImageDead(deadBody.color),
            deadBody.position.y * 15 + 1, deadBody.position.x * 15 + 1, 15, 15, null))
          gameCollectionables.foreach(collectionable => g.drawImage(coin, collectionable.position.y * 15 + 1,
            collectionable.position.x * 15 + 1, 15, 15, null))
          gamePlayers.foreach {
            case ap: AlivePlayer => drawCrewmate(g, ap, getImageAlive(ap.color))
            case dp: DeadPlayer =>  drawCrewmate(g, dp, getImageGhost(dp.color))
          }
      }
    }

    def updateGame(myChar: Player,
                   players: Seq[Player],
                   collectionables : Seq[Collectionable],
                   deadBodies : Seq[DeadBody]): Unit = {
      this.gameMyChar = myChar
      this.gamePlayers = players
      this.gameCollectionables = collectionables
      this.gameDeadBodies = deadBodies
      repaint()
    }

    private def getImageAlive(color : String) : BufferedImage =  {
      ImageIO.read(getClass.getResourceAsStream(s"/images/playerAlive${color}.png"))
    }

    private def getImageGhost(color : String) : BufferedImage =  {
      ImageIO.read(getClass.getResourceAsStream(s"/images/playerGhost${color}.png"))
    }

    private def getImageDead(color : String) : BufferedImage =  {
      ImageIO.read(getClass.getResourceAsStream(s"/images/playerDead${color}.png"))
    }

    private def drawImpostor(g : Graphics, player : Player, image : BufferedImage) : Unit = {
      g.drawImage(image, player.position.y * 15 + 1, player.position.x * 15 + 1, 20, 20, null)
      g.drawString(player.username.toUpperCase, player.position.y * 15 - 10, player.position.x * 15 - 5)
    }

    private def drawCrewmate(g : Graphics, player : Player, image : BufferedImage) : Unit = {
      g.drawImage(image, player.position.y * 15 + 1, player.position.x * 15 + 1, 20, 20, null)
      g.drawString(player.username.toLowerCase(), player.position.y * 15 - 10, player.position.x * 15 - 5)
    }
  }
}