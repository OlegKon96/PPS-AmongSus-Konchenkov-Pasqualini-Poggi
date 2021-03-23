package it.amongsus.core.player

/**
 * Trait that manages the Impostor.
 */
trait Impostor {
  self: Player =>

  /**
   * activates or disactivates a field of view sabotage.
   * @param players sequence of game players.
   * @param state Boolean inditating if sabotage is On or Off.
   * @return sequence of players whit modified field of view.
   */
  def sabotage(players: Seq[Player], state: Boolean): Seq[Player] = {
    var newPlayers: Seq[Player] = Seq.empty
    players.foreach {
      case aliveCrewmate: CrewmateAlive =>
        newPlayers = newPlayers :+ CrewmateAlive(aliveCrewmate.color, aliveCrewmate.emergencyCalled,
          if(state) Constants.Crewmate.FIELD_OF_VIEW_SABOTAGE else Constants.Crewmate.FIELD_OF_VIEW,
          aliveCrewmate.clientId, aliveCrewmate.username, aliveCrewmate.numCoins, aliveCrewmate.position)
      case _ =>
    }
    newPlayers
  }
}