package it.amongsus.view.swingio

import cats.effect.IO
import java.awt.event.FocusListener
import javax.swing.JTextField

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JTextField]] in the form
 * of IO monad in a purely functional style.
 *
 * @param component the JTextField that this class wraps.
 */
class JTextFieldIO(override val component: JTextField) extends ComponentIO(component){
  /**
   * Monadic description of Swing's getText method
   */
  def text: IO[String] = IO {component.getText()}

  /**
   * Monadic description of Swing's addFocusListener method
   * @param focusListener
   */
  def addFocusListener(focusListener:FocusListener) : IO[Unit] = IO {component.addFocusListener(focusListener)}

  /**
   * Monadic description of Swing's setText("") method
   */
  def clearText(): IO[Unit] = IO { component.setText("") }
}

/**
 * Factory for JTextFieldIO instances
 */
object JTextFieldIO {
  def apply(): IO[JTextFieldIO] = IO { new JTextFieldIO(new JTextField())}
}