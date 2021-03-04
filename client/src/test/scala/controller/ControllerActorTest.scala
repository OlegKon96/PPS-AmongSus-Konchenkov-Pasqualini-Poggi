package controller

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import it.amongsus.controller.actor.{ControllerActor, LobbyActorInfo}
import it.amongsus.messages.GameMessageClient._
import it.amongsus.messages.GameMessageServer.{LeaveGameServer, PlayerReadyServer}
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.messages.LobbyMessagesServer._
import it.amongsus.view.actor.UiActorLobbyMessages._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class ControllerActorTest extends TestKit(ActorSystem("test", ConfigFactory.load("test")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)
  private val NUM_PLAYERS = 4

  "The Controller Actor" should {

    /*"Get Ready, Win a Match and then Leave the Client" in {
      val client = TestProbe()
      val controllerActor =
        system.actorOf(ControllerActor.props(LobbyActorInfo.apply(Option(client.ref))))
      controllerActor ! MatchFound(client.ref)
      client.expectMsgType[MatchFoundUi]
      controllerActor ! PlayerReadyClient()
      client.expectMsgType[PlayerReadyServer]
      controllerActor ! GameEndClient(Win())
      client.expectMsgType[GameWonUi]
      controllerActor ! LeaveGameClient()
      client.expectMsgType[LeaveGameServer]
    }*/

    "Add a User to a Lobby Client" in {
      val client = TestProbe()
      val controllerActor = system.actorOf(ControllerActor.props(LobbyActorInfo.apply(Option(client.ref))))
      controllerActor ! UserAddedToLobbyClient(NUM_PLAYERS, NUM_PLAYERS)
      client.expectMsgType[UserAddedToLobbyUi]
    }

    "Update the Lobby Client" in {
      val client = TestProbe()
      val controllerActor = system.actorOf(ControllerActor.props(LobbyActorInfo.apply(Option(client.ref))))
      controllerActor ! UpdateLobbyClient(NUM_PLAYERS)
      client.expectMsgType[UpdateLobbyClient]
    }

    "Create a Private Lobby Client" in {
      val client = TestProbe()
      val controllerActor = system.actorOf(ControllerActor.props(LobbyActorInfo.apply(Option(client.ref))))
      controllerActor ! PrivateLobbyCreatedClient("asdasdasd", NUM_PLAYERS)
      client.expectMsgType[PrivateLobbyCreatedUi]
    }

    "Match Found" in {
      val client = TestProbe()
      val controllerActor =
        system.actorOf(ControllerActor.props(LobbyActorInfo.apply(Option(client.ref))))
      controllerActor ! MatchFound(client.ref)
      client.expectMsgType[MatchFoundUi]
    }

  }
}