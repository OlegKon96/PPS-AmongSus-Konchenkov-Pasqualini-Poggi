package lobby

import akka.actor.ActorRef
import it.amongsus.server.common.{GamePlayer, Player}
import it.amongsus.server.lobby.{GameLobby, Lobby}
import org.scalamock.scalatest.MockFactory
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * Class that tests the lobby.
 */
class LobbyTest extends AnyWordSpecLike with MockFactory {

  private val NUM_PLAYERS = 4

  "The lobby" should {
    "create an empty lobby" in {
      val lobby = this.create2PlayersLobby
      assert(lobby.players.isEmpty)
    }

    "add a player to it" in {
      val lobby = new GameLobby[Player](NUM_PLAYERS)
      assert(lobby.players.isEmpty)
      val updatedLobby = lobby.addPlayer(mock[Player])
      assert(updatedLobby.players.length == 1)
    }

    "haven't enough players if it is empty" in {
      val lobby = this.create2PlayersLobby
      assert(!lobby.hasEnoughPlayers)
    }

    "have sufficiently player after adding enough of they" in {
      var lobby = create2PlayersLobby
      lobby = lobby.addPlayer(mock[Player])
      lobby = lobby.addPlayer(mock[Player])
      assert(lobby.hasEnoughPlayers)
    }

    "remove an added player if he disconnected" in {
      var lobby = create2PlayersLobby
      val playerName = "asdasdasd"
      val player = GamePlayer(playerName, playerName, ActorRef.noSender)
      lobby = lobby.addPlayer(player)
      assert(lobby.players.length == 1)
      lobby = lobby.removePlayer(playerName)
      assert(lobby.players.isEmpty)
    }

    "extract players to play game if they are enough" in {
      var lobby = create2PlayersLobby
      lobby = lobby.addPlayer(mock[Player])
      lobby = lobby.addPlayer(mock[Player])
      lobby = lobby.addPlayer(mock[Player])
      val result = lobby.extractPlayersForMatch()
      assert(result.second.isDefined && result.second.get.length == 2)
      assertResult(1)(result.first.players.length)
    }

    "return nothing in the second extraction if players aren't enough" in {
      var lobby = create2PlayersLobby
      lobby = lobby.addPlayer(mock[Player])
      lobby = lobby.addPlayer(mock[Player])
      lobby = lobby.addPlayer(mock[Player])
      val result = lobby.extractPlayersForMatch()
      assert(result.second.isDefined && result.second.get.length == 2)
      assertResult(1)(result.first.players.length)
      assert(result.first.extractPlayersForMatch().second.isEmpty)
    }
  }

  private def create2PlayersLobby: Lobby[Player] = {
    GameLobby(2)
  }
}