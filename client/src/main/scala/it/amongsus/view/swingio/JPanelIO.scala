package it.amongsus.view.swingio

import cats.effect.IO

import java.awt.Color
import java.awt.event.KeyListener
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
  def setBorder(border : Border) : IO[Unit] = IO {component.setBorder(border)}
  def setSize(width: Int, height: Int) : IO[Unit] = IO{component.setSize(width,height)}
  //def paintComponent(g: Graphics) : IO[Unit] = IO{component.paintComponents(g)}
  def setUIInvokingAndWaiting(ui:PanelUI): IO[Unit] = invokeAndWaitIO(component.setUI(ui))
  def addKeyListener(k : KeyListener) : IO[Unit] = IO(component.addKeyListener(k))
  def requestFocusInWindow() : IO[Unit] = IO(component.requestFocusInWindow())
  def setFocusable(boolean: Boolean): IO[Unit] = IO {component.setFocusable(boolean)}
  def background(color : Color) :IO[Unit] = IO {component.setBackground(color)}
}

/**
 * Factory for JPanelIO instances
 */
object JPanelIO{
  def apply(): IO[JPanelIO] = IO { new JPanelIO(new JPanel) }
  def apply(panel : JPanel): IO[JPanelIO] = IO {new JPanelIO(panel)}
}