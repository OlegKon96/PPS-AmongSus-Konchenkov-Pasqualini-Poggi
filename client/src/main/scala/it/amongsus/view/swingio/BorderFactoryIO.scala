package it.amongsus.view.swingio

import cats.effect.IO
import javax.swing.BorderFactory
import javax.swing.border.Border

/**
 * Factory for IO monads that describes how to create some standard [[Border]] objects.
 */
object BorderFactoryIO {
  /**
   * Monadic description of Swing's createEmptyBorder method
   * @param top
   * @param left
   * @param bottom
   * @param right
   */
  def emptyBorderCreated(top: Int, left: Int, bottom: Int, right: Int): IO[Border] = IO {
    BorderFactory.createEmptyBorder(top, left, bottom, right)
  }
}