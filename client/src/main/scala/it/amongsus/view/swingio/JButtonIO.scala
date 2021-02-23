package it.amongsus.view.swingio

import cats.effect.IO
import java.awt.event.{ActionEvent, ActionListener}
import javax.swing.JButton

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JButton]] in the form
 * of IO monad in a purely functional style.
 * @param component the jButton that this class wraps.
 */
class JButtonIO(override val component: JButton) extends ComponentIO(component){
  //procedural event listener description (from API user point of view)
  def addActionListener(l:ActionListener): IO[Unit] = IO {component.addActionListener(l)}
  //event listener that doesn't leverage action event parameter
  def addActionListenerFromUnit(l: => Unit): IO[Unit] = IO {component.addActionListener(_ => l)}
  def removeActionListener(l:ActionListener): IO[Unit] = IO {component.removeActionListener(l)}
  def text(): IO[String] = IO {component.getText}
  def setFocusable(boolean: Boolean): IO[Unit] = IO {component.setFocusable(boolean)}

  //enabling event listener description by monad
  def addActionListener(l:ActionEvent => IO[Unit]): IO[Unit] =
    IO {component.addActionListener( e => l(e).unsafeRunSync() )}
  //event listener that doesn't leverage action event parameter
  def addActionListener(l: => IO[Unit]): IO[Unit] =
    IO {component.addActionListener( _ => l.unsafeRunSync() )}
  def setSize(width : Int, height : Int) : IO[Unit] = IO {component.setSize(width,height)}
  def setText(text: String): IO[Unit] = IO {component.setText(text)}
  def setEnabled(b: Boolean): IO[Unit] = IO { component.setEnabled(b) }
  def setTextInvokingAndWaiting(text: String): IO[Unit] = invokeAndWaitIO(component.setText(text))
  def setEnabledInvokingAndWaiting(b: Boolean): IO[Unit] = invokeAndWaitIO(component.setEnabled(b))
}

/**
 * Factory for JButtonIO instances
 */
object JButtonIO {
  def apply(text:String): IO[JButtonIO] = IO { new JButtonIO(new JButton(text)) }
}