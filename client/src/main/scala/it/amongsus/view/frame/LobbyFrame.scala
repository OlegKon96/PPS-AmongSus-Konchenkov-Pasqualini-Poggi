package it.amongsus.view.frame

import cats.effect.IO

trait LobbyFrame {
  def init(): IO[Unit]
}
