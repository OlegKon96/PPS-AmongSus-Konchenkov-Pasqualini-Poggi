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
 *
 * @param component the jPanel that this class wraps.
 */
class JPanelIO (override val component: JPanel) extends ContainerIO(component) {
  /**
   * Monadic description of Swing's setBorder method
   * @param border
   */
  def setBorder(border : Border) : IO[Unit] = IO {component.setBorder(border)}

  /**
   * Monadic description of Swing's setSize method
   * @param width
   * @param height
   */
  def setSize(width: Int, height: Int) : IO[Unit] = IO{component.setSize(width,height)}

  /**
   * Monadic description of Swing's addKeyListener method
   * @param keyListener
   */
  def addKeyListener(keyListener : KeyListener) : IO[Unit] = IO(component.addKeyListener(keyListener))

  /**
   * Monadic description of Swing's requestFocusInWindow method
   */
  def requestFocusInWindow() : IO[Unit] = IO(component.requestFocusInWindow())

  /**
   * Monadic description of Swing's setFocusable method
   * @param boolean
   */
  def setFocusable(boolean: Boolean): IO[Unit] = IO {component.setFocusable(boolean)}

  /**
   * Monadic description of Swing's setBackground method
   * @param color
   */
  def background(color : Color) :IO[Unit] = IO {component.setBackground(color)}
}

/**
 * Factory for JPanelIO instances
 */
object JPanelIO{
  def apply(): IO[JPanelIO] = IO { new JPanelIO(new JPanel) }
  def apply(panel : JPanel): IO[JPanelIO] = IO {new JPanelIO(panel)}
}