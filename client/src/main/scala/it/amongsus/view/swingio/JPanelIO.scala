package it.amongsus.view.swingio

import cats.effect.IO
import javax.swing.JPanel
import javax.swing.border.Border
import javax.swing.plaf.PanelUI

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JPanel]] in the form
 * of IO monad in a purely functional style.
 * @param component the jPanel that this class wraps.
 */
class JPanelIO (override val component: JPanel) extends ContainerIO(component) {
  def setUI(ui:PanelUI): IO[Unit] = IO {component.setUI(ui)}
  def UI(): IO[PanelUI] = IO {component.getUI}
  def setSize(width: Int, height: Int) : IO[Unit] = IO{component.setSize(width,height)}
  def setBorder(border : Border) : IO[Unit] = IO {component.setBorder(border)}
  def setUIInvokingAndWaiting(ui:PanelUI): IO[Unit] = invokeAndWaitIO(component.setUI(ui))
}

/**
 * Factory for JPanelIO instances
 */
object JPanelIO{
  def apply(): IO[JPanelIO] = IO { new JPanelIO(new JPanel) }
}