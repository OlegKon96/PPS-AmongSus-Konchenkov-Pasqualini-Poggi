package it.amongsus.view.swingio

import cats.effect.IO
import javax.swing.border.Border
import javax.swing.{JLabel, SwingConstants}

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JLabel]] in the form
 * of IO monad in a purely functional style.
 * @param component the jLabel that this class wraps.
 */
class JLabelIO(override val component: JLabel) extends ComponentIO[JLabel](component) {
  def setText(text: String): IO[Unit] = IO {component.setText(text)}
  def text: IO[String] = IO {component.getText}
  def setTextInvokingAndWaiting(text: String): IO[Unit] = invokeAndWaitIO(component.setText(text))
  def setBorder(border: Border): IO[Unit] = IO {component.setBorder(border)}
  def setVisible(boolean: Boolean) : IO[Unit] = IO {component.setVisible(boolean)}
}

/**
 * Factory for JLabelIO instances
 */
object JLabelIO{
  def apply(): IO[JLabelIO] = IO { new JLabelIO(new JLabel) }
  def apply(text:String): IO[JLabelIO] = IO { new JLabelIO(new JLabel(text)) }
}