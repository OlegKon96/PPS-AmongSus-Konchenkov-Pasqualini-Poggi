package it.amongsus.server.common

import akka.actor.ActorRef

/**
 * Trait of the referable
 */
trait Referable {
  def actorRef: ActorRef
}