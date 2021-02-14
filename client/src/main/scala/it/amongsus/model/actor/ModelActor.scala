package it.amongsus.model.actor

import akka.actor.{Actor, ActorLogging, Props}

object ModelActor {
  def props(state: ModelActorInfo): Props =
    Props(new ModelActor(state))
}

class ModelActor(state: ModelActorInfo) extends Actor  with ActorLogging{
  override def receive: Receive = ???
}
