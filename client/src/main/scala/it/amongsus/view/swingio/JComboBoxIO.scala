package it.amongsus.view.swingio

import cats.effect.IO
import javax.swing.JComboBox

/**
 * A class that provides a monadic description of the operations supplied by Swing's [[JComboBox]] in the form
 * of IO monad in a purely functional style.
 *
 * @param component the jComboBox that this class wraps.
 */
class JComboBoxIO(override val component: JComboBox[Int]) extends ComponentIO(component){
  def addItem(item: Int) : IO[Unit] = IO {component.addItem(item)}
  def selectedItem: IO[Int] = IO {component.getItemAt(component.getSelectedIndex)}
}

/**
 * Factory for JButtonIO instances
 */
object JComboBoxIO {
  def apply(): IO[JComboBoxIO] = IO { new JComboBoxIO(new JComboBox[Int])}
}