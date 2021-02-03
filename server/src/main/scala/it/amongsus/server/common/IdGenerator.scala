package it.amongsus.server.common

import java.util.UUID

trait IdGenerator {
  def generateId: String = UUID.randomUUID().toString
}