package it.amongsus.server.common

import akka.actor.ActorRef

/**
 * Trait of the referable to player
 */
trait Referable {
  def actorRef: ActorRef
}