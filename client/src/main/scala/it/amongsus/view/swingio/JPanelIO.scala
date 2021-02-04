package it.amongsus.view.swingio

import cats.effect.IO

import javax.swing.JPanel
import javax.swing.plaf.PanelUI

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JPanel]] in the form
 * of IO monad in a purely functional style.
 * @param component the jPanel that this class wraps.
 */
class JPanelIO (override val component: JPanel) extends ComponentIO(component) {
  def setUI(ui:PanelUI): IO[Unit] = IO {	component.setUI(ui)}
  def UI(): IO[PanelUI] = IO {component.getUI}

  def setUIInvokingAndWaiting(ui:PanelUI): IO[Unit] = invokeAndWaitIO(component.setUI(ui))
}

/** Factory for JPanelIO instances*/
object JPanelIO{
  def apply(): IO[JPanelIO] = IO { new JPanelIO(new JPanel) }
}