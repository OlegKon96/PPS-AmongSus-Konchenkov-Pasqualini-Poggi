package it.amongsus.server.lobby

import it.amongsus.server.common.IdGenerator
import scala.util.Random

/**
 * Trait that manages the generation of the private lobby's code
 */
trait PrivateLobbyCodeGenerator extends IdGenerator {
  private var generatedIds: Seq[String] = Seq.empty

  override def generateId: String = {
    val id = Random.nextInt(100000).toString
    if (generatedIds.contains(id)) {
      generateId
    } else {
      generatedIds = generatedIds :+ id
      id
    }
  }

  def removeId(id: String): Unit = {
    this.generatedIds = this.generatedIds.filter(_ != id)
  }
}