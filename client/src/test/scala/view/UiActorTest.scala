package view

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.view.actor.UiActorLobbyMessages._
import it.amongsus.view.actor.{UiActor, UiActorInfo}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * Class that tests the UiActor
 */
class UiActorTest extends TestKit(ActorSystem("test", ConfigFactory.load("test")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)
  private final val NUM_PLAYERS = 4

  "The UiActor" should {
    "Successfully connected to the server" in {
      val client = TestProbe()
      val uiActor = system.actorOf(UiActor.props(UiActorInfo.apply(Option(client.ref), None)))
      uiActor ! PublicGameSubmitUi("asdasdasd", NUM_PLAYERS)
      client.expectMsgType[JoinPublicLobbyClient]
    }

    "Accept into a private lobby connection with code" in {
      val client = TestProbe()
      val uiActor = system.actorOf(UiActor.props(UiActorInfo.apply(Option(client.ref), None)))
      uiActor ! PrivateGameSubmitUi("asdasdasd", "qwerty")
      client.expectMsgType[JoinPrivateLobbyClient]
    }

    "Create a private lobby" in {
      val client = TestProbe()
      val uiActor = system.actorOf(UiActor.props(UiActorInfo.apply(Option(client.ref), None)))
      uiActor ! CreatePrivateGameSubmitUi("asdasdasd", NUM_PLAYERS)
      client.expectMsgType[CreatePrivateLobbyClient]
    }

    /*"Leave a Lobby" in {
      val client = TestProbe()
      val menuFrame = MenuFrame.apply(Option(client.ref))
      val uiActor = system.actorOf(UiActor.props(UiActorInfo.apply(Option(client.ref), Option(menuFrame))))
      uiActor ! LeaveLobbyUi()
      client.expectMsgType[LeaveLobbyClient]
    }

    "Match Found" in {
      val client = TestProbe()
      val uiActor = system.actorOf(UiActor.props(UiActorInfo.apply(Option(client.ref), None)))
      uiActor ! MatchFoundUi()
      client.expectMsgType[PlayerReadyClient]
    }*/
  }
}