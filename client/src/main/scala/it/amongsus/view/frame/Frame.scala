package it.amongsus.view.frame

import cats.effect.IO

trait Frame{
  def dispose(): IO[Unit]
}