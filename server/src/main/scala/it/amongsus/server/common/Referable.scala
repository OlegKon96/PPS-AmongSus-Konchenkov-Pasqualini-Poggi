package it.amongsus.server.common

import akka.actor.ActorRef

trait Referable {
  def actorRef: ActorRef
}