package it.amongsus.view.panel

import it.amongsus.core.Drawable
import it.amongsus.core.map.{Boundary, Coin, DeadBody, Emergency, Floor, Other, Tile, Vent, Wall}
import it.amongsus.core.player.{CrewmateAlive, CrewmateGhost, ImpostorAlive, ImpostorGhost, Player}
import java.awt.Graphics
import javax.swing.JPanel
import it.amongsus.view.draw.DrawableEntity.drawEntity
import it.amongsus.view.draw.DrawableTile.drawTile

trait GamePanel extends JPanel {
  /**
   * Method that update state of the game
   *
   * @param myChar of the game
   * @param players of the game
   * @param coins of the game
   * @param deadBodies in the game
   */
  def updateGame(myChar: Player, players: Seq[Player], coins : Seq[Coin],
                 deadBodies : Seq[DeadBody]) : Unit
}

object GamePanel {
  def apply(map : Array[Array[Drawable[Tile]]],
            myChar: Player,
            players : Seq[Player],
            coins : Seq[Coin],
            deadBodies : Seq[DeadBody]): GamePanel =
    new GamePanelImpl(map,myChar, players,coins,deadBodies)

  private class GamePanelImpl(map : Array[Array[Drawable[Tile]]],
                              myChar: Player,
                              players : Seq[Player],
                              coins : Seq[Coin],
                              deadBodies : Seq[DeadBody]) extends GamePanel {

    final val WIDTH : Int = 1080
    final val HEIGHT : Int = 750

    private var gamePlayers = players
    private var gameCoins = coins
    private var gameDeadBodies = deadBodies
    private var gameMyChar = myChar
    private val gameMap = map

    override def paintComponent(g : Graphics): Unit = {
      g.clearRect(0, 0, WIDTH, HEIGHT)
      drawTiles(g)
      drawEntities(g)
    }

    private def drawTiles(g:Graphics) : Unit = {
      gameMap.foreach(x => x.foreach {
        case vent: Vent => drawTile(vent,g,gameMyChar)
        case emergency: Emergency => drawTile(emergency,g,gameMyChar)
        case boundary: Boundary => drawTile(boundary,g,gameMyChar)
        case wall: Wall => drawTile(wall,g,gameMyChar)
        case floor: Floor => drawTile(floor,g,gameMyChar)
        case other: Other => drawTile(other,g,gameMyChar)
      })
    }

    private def drawEntities(g: Graphics): Unit ={
      gameMyChar match {
        case impostorAlive: ImpostorAlive =>
          drawEntity(impostorAlive,g,gamePlayers,gameDeadBodies,gameCoins)
        case impostorGhost: ImpostorGhost =>
          drawEntity(impostorGhost,g,gamePlayers,gameDeadBodies,gameCoins)
        case crewmateAlive: CrewmateAlive =>
          drawEntity(crewmateAlive,g,gamePlayers,gameDeadBodies,gameCoins)
        case crewmateGhost: CrewmateGhost =>
          drawEntity(crewmateGhost,g,gamePlayers,gameDeadBodies,gameCoins)
      }
    }

    def updateGame(myChar: Player,
                   players: Seq[Player],
                   coins : Seq[Coin],
                   deadBodies : Seq[DeadBody]): Unit = {
      this.gameMyChar = myChar
      this.gamePlayers = players
      this.gameCoins = coins
      this.gameDeadBodies = deadBodies
      repaint()
    }

  }
}