package it.amongsus.view.swingio

import cats.effect.IO
import javax.swing.JTextArea

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JTextArea]] in the form
 * of IO monad in a purely functional style.
 *
 * @param component the JTextArea that this class wraps.
 */
class JTextAreaIO(override val component: JTextArea) extends ComponentIO(component){
  /**
   * Monadic description of Swing's append method
   * @param text
   */
  def appendText(text: String): IO[Unit] = IO {component.append(text)}

  /**
   * Monadic description of Swing's setAutoscrolls(true) method
   */
  def focus(): Unit = component.setAutoscrolls(true)

  /**
   * Monadic description of Swing's setEditable(false) method
   */
  def setEditable(): Unit = component.setEditable(false)
}

/**
 * Factory for JTextAreaIO instances
 */
object JTextAreaIO {
  def apply(rows: Int, columns: Int): IO[JTextAreaIO] = IO { new JTextAreaIO(new JTextArea(rows, columns)) }
}