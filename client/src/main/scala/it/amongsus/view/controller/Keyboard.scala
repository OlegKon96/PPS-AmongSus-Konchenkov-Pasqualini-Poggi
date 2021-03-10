package it.amongsus.view.controller

import it.amongsus.core.util.Movement._
import it.amongsus.view.frame.GameFrame
import java.awt.event.{KeyEvent, KeyListener}

trait Keyboard extends KeyListener {
  def gameFrame: GameFrame
}

object Keyboard {

  def apply(gameFrame: GameFrame): Keyboard = new KeyboardImpl(gameFrame)

  private[view] class KeyboardImpl(override val gameFrame: GameFrame) extends Keyboard {

    private val UP = KeyEvent.VK_UP
    private val DOWN = KeyEvent.VK_DOWN
    private val RIGHT = KeyEvent.VK_RIGHT
    private val LEFT = KeyEvent.VK_LEFT

    /**
     * For each event panel will draw entities in different way.
     *
     * @param keyEvent a key event happened in panel.
     */
    override def keyPressed(keyEvent: KeyEvent): Unit = {
      keyEvent.getKeyCode match {
        case UP => gameFrame.movePlayer(Up())
        case LEFT => gameFrame.movePlayer(Left())
        case DOWN => gameFrame.movePlayer(Down())
        case RIGHT => gameFrame.movePlayer(Right())
        case _ =>
      }
    }

    override def keyReleased(keyEvent: KeyEvent): Unit = {}

    override def keyTyped(keyEvent: KeyEvent): Unit = {}
  }
}