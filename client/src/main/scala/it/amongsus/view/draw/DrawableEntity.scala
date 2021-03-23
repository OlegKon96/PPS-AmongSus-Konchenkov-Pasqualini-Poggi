package it.amongsus.view.draw

import it.amongsus.core.map.{Coin, DeadBody}
import it.amongsus.core.player._
import java.awt.Graphics
import java.awt.image.BufferedImage

/**
 *
 * @tparam E
 */
trait DrawableEntity[E] {
  def drawEntity(entity : E, g : Graphics, gamePlayers : Seq[Player], gameDeadBodies : Seq[DeadBody],
                 gameCoins : Seq[Coin])
}

object DrawableEntity {
  def drawEntity[E : DrawableEntity](entity : E, g : Graphics, gamePlayers : Seq[Player],
                                     gameDeadBodies : Seq[DeadBody], gameCoins : Seq[Coin]) : Unit =
    implicitly[DrawableEntity[E]].drawEntity(entity,g,gamePlayers,gameDeadBodies,gameCoins)

  implicit object drawCrewmateAlive extends DrawableEntity[CrewmateAlive] {
    override def drawEntity(entity: CrewmateAlive, g : Graphics, gamePlayers : Seq[Player],
                            gameDeadBodies : Seq[DeadBody],
                            gameCoins : Seq[Coin]): Unit = {
      drawDeadBodies(g,gameDeadBodies,entity)
      drawCoins(g,gameCoins,entity)
      gamePlayers.filter(player => player.position.distance(entity.position) < entity.fieldOfView).foreach {
        case alivePlayer: AlivePlayer => drawPlayer(g, alivePlayer, getImageAlive(alivePlayer.color),
          alivePlayer.username.toLowerCase())
        case _ =>
      }
    }
  }

  implicit object drawImpostorAlive extends DrawableEntity[ImpostorAlive] {
    override def drawEntity(entity: ImpostorAlive, g : Graphics, gamePlayers : Seq[Player],
                            gameDeadBodies : Seq[DeadBody],
                            gameCoins : Seq[Coin]): Unit = {
      drawDeadBodies(g,gameDeadBodies,entity)
      gamePlayers.filter(player => player.position.distance(entity.position) < entity.fieldOfView).foreach {
        case impostorAlive: ImpostorAlive =>
          drawPlayer(g, impostorAlive, getImageAlive(impostorAlive.color),
            impostorAlive.username.toUpperCase())
        case crewmateAlive: CrewmateAlive =>
          drawPlayer(g, crewmateAlive, getImageAlive(crewmateAlive.color),
            crewmateAlive.username.toLowerCase())
        case _ =>
      }
    }
  }

  implicit object drawImpostorGhost extends DrawableEntity[ImpostorGhost] {
    override def drawEntity(entity: ImpostorGhost, g : Graphics, gamePlayers : Seq[Player],
                            gameDeadBodies : Seq[DeadBody],
                            gameCoins : Seq[Coin]): Unit = {
      drawDeadBodies(g,gameDeadBodies,entity)
      gamePlayers.foreach {
        case impostorAlive: ImpostorAlive => drawPlayer(g, impostorAlive, getImageAlive(impostorAlive.color),
          impostorAlive.username.toUpperCase())
        case impostorGhost: ImpostorGhost => drawPlayer(g, impostorGhost, getImageGhost(impostorGhost.color),
          impostorGhost.username.toUpperCase())
        case crewmateAlive: CrewmateAlive => drawPlayer(g, crewmateAlive, getImageAlive(crewmateAlive.color),
          crewmateAlive.username.toLowerCase())
        case crewmateGhost: CrewmateGhost => drawPlayer(g, crewmateGhost, getImageGhost(crewmateGhost.color),
          crewmateGhost.username.toLowerCase())
      }
    }
  }

  implicit object drawCrewmateGhost extends DrawableEntity[CrewmateGhost] {
    override def drawEntity(entity: CrewmateGhost, g : Graphics, gamePlayers : Seq[Player],
                            gameDeadBodies : Seq[DeadBody],
                            gameCoins : Seq[Coin]): Unit = {
      drawDeadBodies(g,gameDeadBodies,entity)
      drawCoins(g,gameCoins,entity)
      gamePlayers.foreach {
        case alivePlayer: AlivePlayer => drawPlayer(g, alivePlayer, getImageAlive(alivePlayer.color),
          alivePlayer.username.toLowerCase())
        case deadPlayer: DeadPlayer =>  drawPlayer(g, deadPlayer, getImageGhost(deadPlayer.color),
          deadPlayer.username.toLowerCase())
      }

    }
  }

  private def drawDeadBodies(g : Graphics, gameDeadBodies : Seq[DeadBody],entity : Player): Unit = {
    for {
      deadBody <- gameDeadBodies
      if deadBody.position.distance(entity.position) < entity.fieldOfView
    } paintDeadBody(g, deadBody)
  }

  private def drawCoins(g : Graphics, gameCoins : Seq[Coin], entity: Player): Unit = {
    for {
      coin <- gameCoins
      if coin.position.distance(entity.position) < entity.fieldOfView
    } paintCoin(g, coin)
  }

  private def drawPlayer(g : Graphics, player : Player, image : BufferedImage, username : String): Unit = {
    paintPlayer(g, player, image)
    g.drawString(username, player.position.y * DRAWABLE_SCALING - 10,
      player.position.x * DRAWABLE_SCALING - 5)
  }
}