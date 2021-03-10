package it.amongsus.server.common

import akka.actor.ActorRef

/**
 * Trait of the referable to actor
 */
trait Referable {
  def actorRef: ActorRef
}