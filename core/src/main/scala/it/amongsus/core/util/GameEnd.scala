package it.amongsus.core.util

import it.amongsus.core.player.Player

/**
 * Trait that manages the End of the game.
 */
trait GameEnd{
  /**
   * Players of the game.
   *
   * @return sequence of players.
   */
  def players: Seq[Player]
  /**
   * The winning team.
   *
   * @return winner crew.
   */
  def crew: WinnerCrew
}

/**
 * Trait that manages the Winning of a Crew.
 */
trait WinnerCrew

object GameEnd{
  case class Win(override val players: Seq[Player], override val crew:WinnerCrew) extends GameEnd

  case class Lost(override val players: Seq[Player], override val crew:WinnerCrew) extends GameEnd

  case object ImpostorCrew extends WinnerCrew

  case object CrewmateCrew extends WinnerCrew
}