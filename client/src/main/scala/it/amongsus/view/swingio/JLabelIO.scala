package it.amongsus.view.swingio

import cats.effect.IO
import javax.swing.border.Border
import javax.swing.JLabel

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JLabel]] in the form
 * of IO monad in a purely functional style.
 *
 * @param component the jLabel that this class wraps.
 */
class JLabelIO(override val component: JLabel) extends ComponentIO[JLabel](component) {
  /**
   * Monadic description of Swing's setText method
   * @param text
   */
  def setText(text: String): IO[Unit] = IO {component.setText(text)}

  /**
   * Monadic description of Swing's getText method
   */
  def text: IO[String] = IO {component.getText}

  /**
   * Monadic description using InvokeAndWaiting of Swing's setText method
   * @param text
   */
  def setTextInvokingAndWaiting(text: String): IO[Unit] = invokeAndWaitIO(component.setText(text))

  /**
   * Monadic description of Swing's setBorder method
   * @param border
   */
  def setBorder(border: Border): IO[Unit] = IO {component.setBorder(border)}

  /**
   * Monadic description of Swing's setVisible method
   * @param boolean
   */
  def setVisible(boolean: Boolean) : IO[Unit] = IO {component.setVisible(boolean)}
}

/**
 * Factory for JLabelIO instances
 */
object JLabelIO{
  def apply(): IO[JLabelIO] = IO { new JLabelIO(new JLabel) }
  def apply(text:String): IO[JLabelIO] = IO { new JLabelIO(new JLabel(text)) }
}