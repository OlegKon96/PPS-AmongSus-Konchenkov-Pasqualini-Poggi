package it.amongsus.view.swingio

import cats.effect.IO
import java.awt.event.{ComponentAdapter, ComponentEvent, ComponentListener, MouseListener}
import java.awt.{Component, Dimension, Font}

/**
 * A class that provides a monadic description of the operations supplied by awt's [[Component]] in the form
 * of IO monad in a purely functional style. This package provided some ad-hoc factory utilities for the most
 * popular Swing's components.
 *
 * @param component the component that this class wraps.
 * @tparam T the type of the component to be wrapped. and whose methods are to be enhanced with IO description.
 */
class ComponentIO[T<:Component](val component: T){

  /**
   * Monadic description of Swing's addComponentListener method
   * @param l the ComponentListener to be added
   */
  def addComponentListener(l: ComponentListener): IO[Unit] = IO {component.addComponentListener(l)}

  /**
   * Monadic description of Swing's addMouseListener method
   * @param l the MouseListener to be added
   */
  def addMouseListener(l:MouseListener): IO[Unit] = IO {component.addMouseListener(l)}

  /**
   * Monadic description of Swing's getFont method
   * @return the Font of the component
   */
  def font(): IO[Font] = IO {component.getFont}

  /**
   * Monadic description of Swing's setPreferredSize method
   * @param dimension the Dimension of the component
   */
  def setPreferredSize(dimension: Dimension): IO[Unit] = IO {component.setPreferredSize(dimension)}

  /**
   * Monadic description using InvokeAndWaiting of Swing's setPreferredSize method
   * @param dimension the
   */
  def setPreferredSizeInvokingAndWaiting(dimension: Dimension): IO[Unit] =
    invokeAndWaitIO(component.setPreferredSize(dimension))

}