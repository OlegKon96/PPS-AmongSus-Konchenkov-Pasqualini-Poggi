package it.amongsus.core.entities.player

/**
 * Trait that manages the Impostor
 */
trait Impostor {
  self: Player =>

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