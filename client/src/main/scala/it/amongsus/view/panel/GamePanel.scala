package it.amongsus.view.panel

import javax.swing.JPanel

trait GamePanel extends JPanel{

}

object GamePanel {
  def apply() : GamePanel = new GamePanelImpl()

  private class GamePanelImpl() extends GamePanel {
    
  }
}
