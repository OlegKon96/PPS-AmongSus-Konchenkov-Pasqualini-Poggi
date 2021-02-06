package lobby

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import it.amongsus.messages.LobbyMessagesClient.Connected
import it.amongsus.messages.LobbyMessagesServer.{ConnectServer, CreatePrivateLobbyServer, JoinPrivateLobbyServer}
import it.amongsus.messages.LobbyMessagesServer.{JoinPublicLobbyServer, LobbyError, LobbyErrorOccurred}
import it.amongsus.messages.LobbyMessagesServer.{PrivateLobbyCreated, UserAddedToLobby}
import it.amongsus.server.lobby.LobbyManagerActor
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * Class that tests the actor lobby manager.
 */
class ActorLobbyManagerTest extends TestKit(ActorSystem("test", ConfigFactory.load("test")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)
  private val NUM_PLAYERS = 4

  "The lobby actor" should {
    "successfully connected to the server" in {
      val lobbyActor = system.actorOf(LobbyManagerActor.props())
      val client = TestProbe()
      lobbyActor ! ConnectServer(client.ref)
      client.expectMsgType[Connected]
    }

    "accept a public lobby connection" in {
      val lobbyActor = system.actorOf(LobbyManagerActor.props())
      val client = TestProbe()
      lobbyActor ! ConnectServer(client.ref)
      val id = client.expectMsgPF() { case Connected(id) => id }
      lobbyActor ! JoinPublicLobbyServer(id, "user", NUM_PLAYERS)
      client.expectMsgType[UserAddedToLobby]
    }

    "create a private lobby" in {
      val lobbyActor = system.actorOf(LobbyManagerActor.props())
      val client = TestProbe()
      lobbyActor ! ConnectServer(client.ref)
      val id = client.expectMsgPF() { case Connected(id) => id }
      lobbyActor ! CreatePrivateLobbyServer(id, "user", NUM_PLAYERS)
      client.expectMsgType[PrivateLobbyCreated]
    }

    "accept request connection to a private lobby if the private lobby exists and work properly" in {
      val lobbyActor = system.actorOf(LobbyManagerActor.props())
      val client = TestProbe()
      lobbyActor ! ConnectServer(client.ref)
      val firstClientId = client.expectMsgPF() { case Connected(id) => id }
      lobbyActor ! CreatePrivateLobbyServer(firstClientId, "user", NUM_PLAYERS)
      val secondPlayer = TestProbe()
      val lobbyCode = client.expectMsgPF() { case PrivateLobbyCreated(lobbyCode) => lobbyCode }
      lobbyActor ! ConnectServer(secondPlayer.ref)
      val secondClientId = secondPlayer.expectMsgPF() { case Connected(secondId) => secondId }
      secondPlayer.send(lobbyActor, JoinPrivateLobbyServer(secondClientId, "secondPlayer", lobbyCode))
      secondPlayer.expectMsgType[UserAddedToLobby]
    }
  }

  "send a message of error every time any user tries to join an incomparable private lobby" in {
    val lobbyActor = TestActorRef[LobbyManagerActor](LobbyManagerActor.props())
    val client = TestProbe()
    lobbyActor ! ConnectServer(client.ref)
    val id = client.expectMsgPF() { case Connected(id) => id }
    lobbyActor ! JoinPrivateLobbyServer(id, "user", "qwertyasdasdasdasd")
    client.expectMsg(LobbyErrorOccurred(LobbyError.PrivateLobbyIdNotValid))
  }
}