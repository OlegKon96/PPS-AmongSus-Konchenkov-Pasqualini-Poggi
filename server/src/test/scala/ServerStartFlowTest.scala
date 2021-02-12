import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import it.amongsus.messages.LobbyMessagesClient.{Connected, UserAddedToLobbyClient}
import it.amongsus.messages.LobbyMessagesServer.{ConnectServer, JoinPublicLobbyServer, MatchFound}
import it.amongsus.server.lobby.LobbyManagerActor
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * Class that tests the server test flow.
 */
class ServerStartFlowTest extends TestKit(ActorSystem("test", ConfigFactory.load("test")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  private val NUM_PLAYERS = 2
  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)
  "The correct process" should {
    "add users to the lobby, and if there are enough players, start the game and notify the players" in {
      val actorOfLobby = TestActorRef[LobbyManagerActor](LobbyManagerActor.props())
      val firstClient = TestProbe()
      val secondClient = TestProbe()

      actorOfLobby ! ConnectServer(firstClient.ref)
      actorOfLobby ! ConnectServer(secondClient.ref)

      val clientOne = firstClient.expectMsgPF() { case Connected(client1Id) => client1Id }
      val clientTwo = secondClient.expectMsgPF() { case Connected(client2Id) => client2Id }

      actorOfLobby ! JoinPublicLobbyServer(clientOne, "me", NUM_PLAYERS)
      actorOfLobby ! JoinPublicLobbyServer(clientTwo, "me2", NUM_PLAYERS)

      firstClient.expectMsgType[UserAddedToLobbyClient]
      secondClient.expectMsgType[UserAddedToLobbyClient]
      firstClient.expectMsgType[UserAddedToLobbyClient]
      firstClient.expectMsgType[MatchFound]
      secondClient.expectMsgType[MatchFound]
    }
  }
}