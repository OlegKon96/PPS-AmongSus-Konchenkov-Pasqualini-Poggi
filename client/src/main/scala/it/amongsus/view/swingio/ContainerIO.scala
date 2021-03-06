package it.amongsus.view.swingio

import cats.effect.IO
import java.awt.{Component, Container, LayoutManager}

/**
 * A class that provides a monadic description of the operations supplied by awt's [[Container]] in the form
 * of IO monad in a purely functional style.
 * This package provided some ad-hoc factory utilities for the most popular Swing's containers.
 * @param component the container that this class wraps.
 * @tparam T the type of the component to be wrapped. and whose methods are to be enhanced with IO description.
 */
class ContainerIO[T<:Container](override val component: T) extends ComponentIO(component) {
  def add(componentToBeAdded: ComponentIO[_<:Component]): IO[Component] =
    IO {component.add(componentToBeAdded.component)}
  def add(name: String, componentToBeAdded: ComponentIO[_<:Component]): IO[Component] =
    IO {component.add(name, componentToBeAdded.component)}
  def add(componentToBeAdded: ComponentIO[_<:Component], constraints : Object): IO[Unit] =
    IO {component.add(componentToBeAdded.component, constraints)}
  def remove(componentToBeAdded: ComponentIO[_<:Component]): IO[Unit] =
    IO {component.remove(componentToBeAdded.component)}
  def removeAll(): IO[Unit] = IO {component.removeAll()}
  def setLayout(mgr : LayoutManager): IO[Unit] = IO {component.setLayout(mgr)}
  //versions with invokeAndWait for finer granularity in thread assignment
  def addInvokingAndWaiting(componentToBeAdded: ComponentIO[_<:Component]): IO[Unit] =
    invokeAndWaitIO(component.add(componentToBeAdded.component))
  def addInvokingAndWaiting(name: String, componentToBeAdded: ComponentIO[_<:Component]): IO[Unit] =
    invokeAndWaitIO(component.add(name, componentToBeAdded.component))
  def addInvokingAndWaiting(componentToBeAdded: ComponentIO[_<:Component], constraints : Object): IO[Unit] =
    invokeAndWaitIO(component.add(componentToBeAdded.component, constraints))
  def removeInvokingAndWaiting(componentToBeAdded: ComponentIO[_<:Component]): IO[Unit] =
    invokeAndWaitIO(component.remove(componentToBeAdded.component))
  def removeAllInvokingAndWaiting(): IO[Unit] = invokeAndWaitIO(component.removeAll())
  def setLayoutInvokingAndWaiting(mgr : LayoutManager): IO[Unit] = invokeAndWaitIO(component.setLayout(mgr))
}