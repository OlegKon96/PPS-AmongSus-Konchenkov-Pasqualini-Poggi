package it.amongsus.view.draw

import it.amongsus.core.map.{Collectionable, DeadBody}
import it.amongsus.core.player.{AlivePlayer, CrewmateAlive, CrewmateGhost,
  DeadPlayer, ImpostorAlive, ImpostorGhost, Player}

import java.awt.Graphics

trait DrawableEntity[E] {
  def draw(entity : E,g : Graphics, gamePlayers : Seq[Player], gameDeadBodies : Seq[DeadBody],
           gameCollectionables : Seq[Collectionable])
}

object DrawableEntity {

  def draw[E : DrawableEntity](elem : E, g : Graphics, gamePlayers : Seq[Player], gameDeadBodies : Seq[DeadBody],
                               gameCollectionables : Seq[Collectionable]) : Unit =
    implicitly[DrawableEntity[E]].draw(elem,g,gamePlayers,gameDeadBodies,gameCollectionables)

  implicit object drawCrewmateAlive extends DrawableEntity[CrewmateAlive] {
    override def draw(entity: CrewmateAlive,g : Graphics, gamePlayers : Seq[Player], gameDeadBodies : Seq[DeadBody],
                      gameCollectionables : Seq[Collectionable]): Unit = {
      gameDeadBodies.filter(body => body.position.distance(entity.position) < entity.fieldOfView)
        .foreach(deadBody => g.drawImage(getImageDead(deadBody.color), deadBody.position.y * DRAWABLE_SCALING + 1,
          deadBody.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null))
      gameCollectionables.filter(collectionable => collectionable.position.distance(entity.position) <
        entity.fieldOfView).foreach(collectionable => g.drawImage(COIN, collectionable.position.y * DRAWABLE_SCALING + 1,
        collectionable.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null))
      gamePlayers.filter(player => player.position.distance(entity.position) < entity.fieldOfView).foreach {
        case alivePlayer: AlivePlayer => drawCrewmate(g, alivePlayer, getImageAlive(alivePlayer.color))
        case _ =>
      }
    }
  }

  implicit object drawImpostorAlive extends DrawableEntity[ImpostorAlive] {
    override def draw(entity: ImpostorAlive,g : Graphics, gamePlayers : Seq[Player], gameDeadBodies : Seq[DeadBody],
                      gameCollectionables : Seq[Collectionable]): Unit = {
      gameDeadBodies.filter(body => body.position.distance(entity.position) < entity.fieldOfView)
        .foreach(deadBody => g.drawImage(getImageDead(deadBody.color), deadBody.position.y * DRAWABLE_SCALING + 1,
          deadBody.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null))
      gamePlayers.filter(player => player.position.distance(entity.position) < entity.fieldOfView).foreach {
        case impostorAlive: ImpostorAlive =>
          drawImpostor(g, impostorAlive, getImageAlive(impostorAlive.color))
        case crewmateAlive: CrewmateAlive =>
          drawCrewmate(g, crewmateAlive, getImageAlive(crewmateAlive.color))
        case _ =>
      }
    }
  }

  implicit object drawImpostorGhost extends DrawableEntity[ImpostorGhost] {
    override def draw(entity: ImpostorGhost,g : Graphics, gamePlayers : Seq[Player], gameDeadBodies : Seq[DeadBody],
                      gameCollectionables : Seq[Collectionable]): Unit = {
      gameDeadBodies.foreach(deadBody => g.drawImage(getImageDead(deadBody.color), deadBody.position.y * DRAWABLE_SCALING + 1,
        deadBody.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null))
      gamePlayers.foreach {
        case impostorAlive: ImpostorAlive => drawImpostor(g, impostorAlive, getImageAlive(impostorAlive.color))
        case impostorGhost: ImpostorGhost => drawImpostor(g, impostorGhost, getImageGhost(impostorGhost.color))
        case crewmateAlive: CrewmateAlive => drawCrewmate(g, crewmateAlive, getImageAlive(crewmateAlive.color))
        case crewmateGhost: CrewmateGhost => drawCrewmate(g, crewmateGhost, getImageGhost(crewmateGhost.color))
      }
    }
  }

  implicit object drawCrewmateGhost extends DrawableEntity[CrewmateGhost] {
    override def draw(entity: CrewmateGhost,g : Graphics, gamePlayers : Seq[Player], gameDeadBodies : Seq[DeadBody],
                      gameCollectionables : Seq[Collectionable]): Unit = {
      gameDeadBodies.foreach(deadBody => g.drawImage(getImageDead(deadBody.color),
        deadBody.position.y * DRAWABLE_SCALING + 1, deadBody.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null))
      gameCollectionables.foreach(collectionable => g.drawImage(COIN, collectionable.position.y * DRAWABLE_SCALING + 1,
        collectionable.position.x * DRAWABLE_SCALING + 1, DRAWABLE_SCALING, DRAWABLE_SCALING, null))
      gamePlayers.foreach {
        case ap: AlivePlayer => drawCrewmate(g, ap, getImageAlive(ap.color))
        case dp: DeadPlayer =>  drawCrewmate(g, dp, getImageGhost(dp.color))
      }

    }
  }
}
