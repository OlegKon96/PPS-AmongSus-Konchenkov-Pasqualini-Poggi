package it.amongsus.view.swingio

import cats.effect.IO
import java.awt.event.{KeyListener, WindowListener}
import java.awt.{Color, Component, Container}
import javax.swing.JFrame

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JFrame]] in the form
 * of IO monad in a purely functional style.
 * @param component the jFrame that this class wraps.
 */
class JFrameIO(override val component: JFrame) extends ContainerIO(component) {
  def contentPane(): IO[ContainerIO[Container]] = IO { new ContainerIO (component.getContentPane) }
  def setSize(width: Int, height: Int): IO[Unit] = IO { component.setSize(width, height)}
  def setDefaultCloseOperation(operation:Int): IO[Unit] = IO {component.setDefaultCloseOperation(operation)}
  def setTitle(title: String): IO[Unit] = IO{component.setTitle(title)}
  def dispose() : IO[Unit] = IO{component.dispose()}
  def setVisible(b: Boolean): IO[Unit] = IO(component.setVisible(b))
  def setResizable(resizable: Boolean): IO[Unit] = IO(component.setResizable(resizable))
  def addKeyListener(k : KeyListener) : IO[Unit] = IO(component.addKeyListener(k))
  def addWindowListener(w : WindowListener) : IO[Unit] = IO {component.addWindowListener(w)}
  def requestFocusInWindow() : IO[Unit] = IO(component.requestFocusInWindow())
  def background(color : Color) :IO[Unit] = IO {component.setBackground(color)}
  //invoke and wait versions (for finer granularity for task assignment to EDT thread)
  def setResizableInvokingAndWaiting(resizable: Boolean): IO[Unit] = invokeAndWaitIO(component.setResizable(resizable))
  def setVisibleInvokingAndWaiting(b: Boolean): IO[Unit] = invokeAndWaitIO(component.setVisible(b))
  def contentPaneInvokingAndWaiting(): IO[Unit] = invokeAndWaitIO(new ContainerIO (component.getContentPane))
  def setSizeInvokingAndWaiting(width: Int, height: Int): IO[Unit] = invokeAndWaitIO(component.setSize(width, height))
  def setLocationRelativeToInvokingAndWaiting(c:Component): IO[Unit] =
    invokeAndWaitIO(component.setLocationRelativeTo(c))
  def setDefaultCloseOperationSetInvokingAndWaiting(operation:Int): IO[Unit] =
    invokeAndWaitIO(component.setDefaultCloseOperation(operation))
  def setTitleInvokingAndWaiting(title: String): IO[Unit] =invokeAndWaitIO(component.setTitle(title))
}

/**
 * Factory for JFrameIO instances
 */
object JFrameIO{
  def apply(): IO[JFrameIO] = IO { new JFrameIO(new JFrame) }
}