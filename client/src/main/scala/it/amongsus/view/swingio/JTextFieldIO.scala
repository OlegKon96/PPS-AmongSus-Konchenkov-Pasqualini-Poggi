package it.amongsus.view.swingio

import cats.effect.IO
import java.awt.event.FocusListener
import javax.swing.JTextField

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JTextField]] in the form
 * of IO monad in a purely functional style.
 * @param component the JTextField that this class wraps.
 */
class JTextFieldIO(override val component: JTextField) extends ComponentIO(component){
  def text: IO[String] = IO {component.getText()}
  def addFocusListener(l:FocusListener) : IO[Unit] = IO {component.addFocusListener(l)}
}

/**
 * Factory for JTextFieldIO instances
 */
object JTextFieldIO {
  def apply(): IO[JTextFieldIO] = IO { new JTextFieldIO(new JTextField())}
}