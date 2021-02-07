package it.amongsus.view.frame

import cats.effect.IO

trait LobbyFrame {
  def start(): IO[Unit]
}

object LobbyFrame {

  def apply(menuView: MenuFrame): LobbyFrame = new LobbyFrameImpl(menuView)

  private class LobbyFrameImpl(menuView: MenuFrame) extends LobbyFrame {
    override def start(): IO[Unit] = ???
  }

}