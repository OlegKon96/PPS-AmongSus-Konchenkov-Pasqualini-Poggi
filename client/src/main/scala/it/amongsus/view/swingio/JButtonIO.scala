package it.amongsus.view.swingio

import cats.effect.IO
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JButton

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JButton]] in the form
 * of IO monad in a purely functional style.
 *
 * @param component the jButton that this class wraps.
 */
class JButtonIO(override val component: JButton) extends ComponentIO(component){
  /**
   * Monadic description  of Swing's addActionListener method
   * @param l the ActionListener to be added
   */
  def addActionListener(l:ActionListener): IO[Unit] = IO {component.addActionListener(l)}

  /**
   * Monadic description  of Swing's removeActionListener method
   * @param l the ActionListener to be removed
   */
  def removeActionListener(l:ActionListener): IO[Unit] = IO {component.removeActionListener(l)}

  /**
   * Monadic description of Swing's getText method
   */
  def text(): IO[String] = IO {component.getText}

  /**
   * Monadic description of Swing's setFocusable method
   * @param boolean
   */
  def setFocusable(boolean: Boolean): IO[Unit] = IO {component.setFocusable(boolean)}
  //enabling event listener description by monad
  /**
   * Monadic description of Swing's addActionListener method
   * @param l
   */
  def addActionListener(l:ActionEvent => IO[Unit]): IO[Unit] =
    IO {component.addActionListener( e => l(e).unsafeRunSync() )}
  //event listener that doesn't leverage action event parameter
  /**
   * Monadic description of Swing's addActionListener method
   * @param l
   */
  def addActionListener(l: => IO[Unit]): IO[Unit] =
    IO {component.addActionListener( _ => l.unsafeRunSync() )}

  /**
   * Monadic description of Swing's setSize method
   * @param width
   * @param height
   */
  def setSize(width : Int, height : Int) : IO[Unit] = IO {component.setSize(width,height)}

  /**
   * Monadic description of Swing's setText method
   * @param text
   */
  def setText(text: String): IO[Unit] = IO {component.setText(text)}

  /**
   * Monadic description of Swing's setEnabled method
   * @param b
   */
  def setEnabled(b: Boolean): IO[Unit] = IO { component.setEnabled(b) }

  /**
   * Monadic description of Swing's setVisible method
   * @param b
   */
  def setVisible(b : Boolean) : IO[Unit] = IO {component.setVisible(b)}

  /**
   * Monadic description using InvokeAndWaiting of Swing's setText method
   * @param text
   */
  def setTextInvokingAndWaiting(text: String): IO[Unit] = invokeAndWaitIO(component.setText(text))

  /**
   *  Monadic description using InvokeAndWaiting of Swing's setEnabled method
   * @param b
   */
  def setEnabledInvokingAndWaiting(b: Boolean): IO[Unit] = invokeAndWaitIO(component.setEnabled(b))
}

/**
 * Factory for JButtonIO instances
 */
object JButtonIO {
  def apply(text:String): IO[JButtonIO] = IO { new JButtonIO(new JButton(text)) }
}