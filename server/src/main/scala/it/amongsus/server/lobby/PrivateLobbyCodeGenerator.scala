package it.amongsus.server.lobby

import it.amongsus.server.common.IdGenerator
import scala.util.Random

/**
 * Trait that manages the generation of the private lobby's code
 */
trait PrivateLobbyCodeGenerator extends IdGenerator {
  private var generatedIds: Seq[String] = Seq.empty
  private final val randomInt = 100000

  override def generateId: String = {
    val id = Random.nextInt(randomInt).toString
    if (generatedIds.contains(id)) {
      generateId
    } else {
      generatedIds = generatedIds :+ id
      id
    }
  }

  /**
   * Method to remove an ID
   *
   * @param id to remove
   */
  def removeId(id: String): Unit = {
    this.generatedIds = this.generatedIds.filter(_ != id)
  }
}