package it.amongsus.view.swingio

import cats.effect.IO
import javax.swing.{JScrollPane, JTextArea}

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JTextArea]] in the form
 * of IO monad in a purely functional style.
 *
 * @param component the JTextArea that this class wraps.
 */
class JScrollPaneIO(override val component: JScrollPane) extends ComponentIO(component){}

/**
 * Factory for JTextAreaIO instances
 */
object JScrollPaneIO {
  def apply(textArea: JTextAreaIO): IO[JScrollPaneIO] = IO { new JScrollPaneIO(new JScrollPane(textArea.component)) }
}