package it.amongsus.server.common

import java.util.UUID

/**
 * Trait of the ID Generator
 */
trait IdGenerator {
  def generateId: String = UUID.randomUUID().toString
}