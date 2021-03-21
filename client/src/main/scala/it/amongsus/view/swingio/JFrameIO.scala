package it.amongsus.view.swingio

import cats.effect.IO
import java.awt.event.{KeyListener, WindowListener}
import java.awt.{Color, Component, Container}
import javax.swing.JFrame

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JFrame]] in the form
 * of IO monad in a purely functional style.
 *
 * @param component the jFrame that this class wraps.
 */
class JFrameIO(override val component: JFrame) extends ContainerIO(component) {
  /**
   * Monadic description of Swing's getContentPane method
   */
  def contentPane(): IO[ContainerIO[Container]] = IO { new ContainerIO (component.getContentPane) }

  /**
   * Monadic description of Swing's setSize method
   * @param width
   * @param height
   */
  def setSize(width: Int, height: Int): IO[Unit] = IO { component.setSize(width, height)}

  /**
   * Monadic description of Swing's setDefaultCloseOperation method
   * @param operation
   */
  def setDefaultCloseOperation(operation:Int): IO[Unit] = IO {component.setDefaultCloseOperation(operation)}

  /**
   * Monadic description of Swing's setTitle method
   * @param title
   */
  def setTitle(title: String): IO[Unit] = IO{component.setTitle(title)}

  /**
   * Monadic description of Swing's dispose method
   */
  def dispose() : IO[Unit] = IO{component.dispose()}

  /**
   * Monadic description of Swing's setVisible method
   * @param boolean
   */
  def setVisible(boolean: Boolean): IO[Unit] = IO(component.setVisible(boolean))

  /**
   * Monadic description of Swing's setResizable method
   * @param boolean
   */
  def setResizable(boolean: Boolean): IO[Unit] = IO(component.setResizable(boolean))

  /**
   * Monadic description of Swing's addKeyListener method
   * @param keyListener
   */
  def addKeyListener(keyListener : KeyListener) : IO[Unit] = IO(component.addKeyListener(keyListener))

  /**
   * Monadic description of Swing's addWindowListener method
   * @param windowListener
   */
  def addWindowListener(windowListener : WindowListener) : IO[Unit] = IO {component.addWindowListener(windowListener)}

  /**
   * Monadic description of Swing's requestFocusInWindow method
   */
  def requestFocusInWindow() : IO[Unit] = IO(component.requestFocusInWindow())

  /**
   * Monadic description of Swing's setBackground method
   * @param color
   */
  def background(color : Color) :IO[Unit] = IO {component.setBackground(color)}

  /**
   * Monadic description using InvokeAndWaiting of Swing's setResizable method
   * @param resizable
   */
  def setResizableInvokingAndWaiting(resizable: Boolean): IO[Unit] = invokeAndWaitIO(component.setResizable(resizable))

  /**
   * Monadic description using InvokeAndWaiting of Swing's setVisible method
   * @param boolean
   */
  def setVisibleInvokingAndWaiting(boolean: Boolean): IO[Unit] = invokeAndWaitIO(component.setVisible(boolean))

  /**
   * Monadic description using InvokeAndWaiting of Swing's getContentPane method
   */
  def contentPaneInvokingAndWaiting(): IO[Unit] = invokeAndWaitIO(new ContainerIO (component.getContentPane))

  /**
   * Monadic description using InvokeAndWaiting of Swing's setSize method
   * @param width
   * @param height
   */
  def setSizeInvokingAndWaiting(width: Int, height: Int): IO[Unit] = invokeAndWaitIO(component.setSize(width, height))

  /**
   * Monadic description using InvokeAndWaiting of Swing's setLocationRelativeTo method
   * @param c
   */
  def setLocationRelativeToInvokingAndWaiting(c:Component): IO[Unit] =
    invokeAndWaitIO(component.setLocationRelativeTo(c))

  /**
   * Monadic description using InvokeAndWaiting of Swing's setDefaultCloseOperation method
   * @param operation
   */
  def setDefaultCloseOperationSetInvokingAndWaiting(operation:Int): IO[Unit] =
    invokeAndWaitIO(component.setDefaultCloseOperation(operation))

  /**
   * Monadic description using InvokeAndWaiting of Swing's setTitle method
   * @param title
   */
  def setTitleInvokingAndWaiting(title: String): IO[Unit] =invokeAndWaitIO(component.setTitle(title))
}

/**
 * Factory for JFrameIO instances
 */
object JFrameIO{
  def apply(): IO[JFrameIO] = IO { new JFrameIO(new JFrame) }
}