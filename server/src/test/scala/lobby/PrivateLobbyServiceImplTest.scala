package lobby

import it.amongsus.server.lobby.PrivateLobbyServiceImpl
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * Class that tests the private lobby service impl.
 */
class PrivateLobbyServiceImplTest extends AnyWordSpecLike {

  private val NUM_PLAYER = 4

  "The private lobby service" should {
    "create a new private lobby" in {
      val lobbyService = new PrivateLobbyServiceImpl()
      val lobby = lobbyService.generateNewPrivateLobby(NUM_PLAYER)
      assertResult(NUM_PLAYER)(lobby.numberOfPlayers)
    }

    "return previously created lobby" in {
      val lobbyService = new PrivateLobbyServiceImpl()
      val lobby = lobbyService.generateNewPrivateLobby(NUM_PLAYER)
      assertResult(Some(lobby))(lobbyService.retrieveExistingLobby(lobby.lobbyId))
    }

    "doesn't found a private lobby that doesn't exist" in {
      val lobbyService = new PrivateLobbyServiceImpl()
      assertResult(None)(lobbyService.retrieveExistingLobby("qwerty"))
    }

    "delete the lobby that was created" in {
      val lobbyService = new PrivateLobbyServiceImpl()
      val lobby = lobbyService.generateNewPrivateLobby(NUM_PLAYER)
      lobbyService.removeLobby(lobby.lobbyId)
      assertResult(None)(lobbyService.retrieveExistingLobby(lobby.lobbyId))
    }
  }
}