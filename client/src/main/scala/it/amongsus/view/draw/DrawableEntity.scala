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
                 gameCollectionables : Seq[Coin])
}

object DrawableEntity {
  def drawEntity[E : DrawableEntity](elem : E, g : Graphics, gamePlayers : Seq[Player], gameDeadBodies : Seq[DeadBody],
                                     gameCollectionables : Seq[Coin]) : Unit =
    implicitly[DrawableEntity[E]].drawEntity(elem,g,gamePlayers,gameDeadBodies,gameCollectionables)

  implicit object drawCrewmateAlive extends DrawableEntity[CrewmateAlive] {
    override def drawEntity(entity: CrewmateAlive, g : Graphics, gamePlayers : Seq[Player],
                            gameDeadBodies : Seq[DeadBody],
                            gameCollectionables : Seq[Coin]): Unit = {
      drawDeadBodies(g,gameDeadBodies,entity)
      drawCollectionables(g,gameCollectionables,entity)
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
                            gameCollectionables : Seq[Coin]): Unit = {
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
                            gameCollectionables : Seq[Coin]): Unit = {
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
                            gameCollectionables : Seq[Coin]): Unit = {
      drawDeadBodies(g,gameDeadBodies,entity)
      drawCollectionables(g,gameCollectionables,entity)
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
    } g.drawImage(getImageDead(deadBody.color), deadBody.position.y * DRAWABLE_SCALING + 1,
      deadBody.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
  }

  private def drawCollectionables(g : Graphics, gameCollectionables : Seq[Coin], entity: Player): Unit = {
    for {
      collectionable <- gameCollectionables
      if collectionable.position.distance(entity.position) < entity.fieldOfView
    } g.drawImage(COIN, collectionable.position.y * DRAWABLE_SCALING + 1,
      collectionable.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null)
  }

  private def drawPlayer(g : Graphics, player : Player, image : BufferedImage, username : String): Unit = {
    g.drawImage(image, player.position.y * DRAWABLE_SCALING + 1, player.position.x * DRAWABLE_SCALING + 1,
      PLAYER_SCALING, PLAYER_SCALING, null)
    g.drawString(username, player.position.y * DRAWABLE_SCALING - 10,
      player.position.x * DRAWABLE_SCALING - 5)
  }
}