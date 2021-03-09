package lobby

import it.amongsus.server.common.GamePlayer
import it.amongsus.server.lobby.{LobbyManager, LobbyManagerImpl, PlayerNumberLobby}
import org.scalamock.scalatest.MockFactory
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * Class that tests the lobby manager.
 */
class ManagerTest extends AnyWordSpecLike with MockFactory {

  private val NUM_PLAYERS = 4

  "The LobbyManager" should {
    "create a new lobby when a new player entered" in {
      val lobbyManager = LobbyManager()
      val player = mock[GamePlayer]
      val lobbyType = PlayerNumberLobby(NUM_PLAYERS)
      lobbyManager.addPlayer(player, lobbyType)
      assert(lobbyManager.getLobby(lobbyType).isDefined)
    }

    "return the lobby previously added" in {
      val lobbyManager = LobbyManager()
      val lobbyType = PlayerNumberLobby(NUM_PLAYERS)
      lobbyManager.addPlayer(mock[GamePlayer], lobbyType)
      assert(lobbyManager.getLobby(lobbyType).isDefined)
    }

    "return nothing if there isn't a lobby created" in {
      val lobbyManager = LobbyManager()
      val lobbyType = PlayerNumberLobby(NUM_PLAYERS)
      assert(lobbyManager.getLobby(lobbyType).isEmpty)
    }

    "return nothing if someone tries to create a match with no players" in {
      val lobbyManager = LobbyManager()
      val lobbyType = PlayerNumberLobby(NUM_PLAYERS/2)
      assert(lobbyManager.attemptExtractPlayerForMatch(lobbyType).isEmpty)
    }

    "return the necessary player (previously added) to start a match" in {
      val lobbyManager = LobbyManager()
      val lobbyType = PlayerNumberLobby(NUM_PLAYERS/2)
      lobbyManager.addPlayer(mock[GamePlayer], lobbyType)
      lobbyManager.addPlayer(mock[GamePlayer], lobbyType)
      assert(lobbyManager.attemptExtractPlayerForMatch(lobbyType).isDefined)
    }

    "return nothing on the second attempt of returning players if are not enough" in {
      val lobbyManager = LobbyManager()
      val lobbyType = PlayerNumberLobby(NUM_PLAYERS/2)
      lobbyManager.addPlayer(mock[GamePlayer], lobbyType)
      lobbyManager.addPlayer(mock[GamePlayer], lobbyType)
      assert(lobbyManager.attemptExtractPlayerForMatch(lobbyType).isDefined)
      assert(lobbyManager.attemptExtractPlayerForMatch(lobbyType).isEmpty)
    }

    "remove from lobby" in {
      val lobbyManager = LobbyManager()
      val lobbyType = PlayerNumberLobby(2)
      val p1 = GamePlayer("1", "asdasdasd", null)
      val p2 = GamePlayer("2", "qwerty", null)
      lobbyManager.addPlayer(p1, lobbyType)
      lobbyManager.removePlayer(p1.id)
      lobbyManager.addPlayer(p1, lobbyType)

      assert(lobbyManager.attemptExtractPlayerForMatch(lobbyType).isEmpty)

      lobbyManager.addPlayer(p2, lobbyType)
      assert(lobbyManager.attemptExtractPlayerForMatch(lobbyType).isDefined)
    }
  }
}