package it.amongsus.view.swingio

import cats.effect.IO
import javax.swing.JComboBox

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