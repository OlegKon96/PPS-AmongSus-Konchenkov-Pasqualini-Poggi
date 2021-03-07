package controller

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import it.amongsus.controller.actor.ControllerActorMessages.{BeginVotingController, GameEndController, TestGameBehaviour, UpdatedPlayersController}
import it.amongsus.controller.actor.{ControllerActor, LobbyActorInfo, LobbyActorInfoData}
import it.amongsus.core.entities.player.{CrewmateAlive, ImpostorAlive}
import it.amongsus.core.entities.util.GameEnd.{CrewmateCrew, Win}
import it.amongsus.core.entities.util.Point2D
import it.amongsus.messages.GameMessageClient._
import it.amongsus.messages.GameMessageServer.{LeaveGameServer, PlayerReadyServer, StartVoting}
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.messages.LobbyMessagesServer._
import it.amongsus.model.actor.ModelActorMessages.{BeginVotingModel, InitModel, KillPlayerModel}
import it.amongsus.model.actor.{ModelActor, ModelActorInfo}
import it.amongsus.view.actor.{UiActor, UiActorInfo}
import it.amongsus.view.actor.UiActorGameMessages.{BeginVotingUi, GameEndUi}
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

    "Get Ready, Win a Match and then Leave the Client" in {
      val serverActor = TestProbe()
      val uiActor = TestProbe()
      val model = TestProbe()

      val controllerActor =
        system.actorOf(ControllerActor.props(LobbyActorInfo(Option(serverActor.ref), Option(uiActor.ref), "dasds")))

      val players = Seq(ImpostorAlive("green", emergencyCalled = false, "dasds", "asdasdsd", Point2D(0,0)))

      controllerActor ! TestGameBehaviour(model.ref, serverActor.ref)

      controllerActor ! PlayerReadyClient()
      serverActor.expectMsgType[PlayerReadyServer]

      controllerActor ! BeginVotingController(Seq())
      serverActor.expectMsgType[StartVoting]
      uiActor.expectMsgType[BeginVotingUi]

      controllerActor ! GameEndController(Win(Seq(),CrewmateCrew()))
      uiActor.expectMsgType[GameEndUi]
    }

    "Add a User to a Lobby Client" in {
      val client = TestProbe()
      val controllerActor = system.actorOf(ControllerActor.props(LobbyActorInfo.apply(Option(client.ref))))
      controllerActor ! UserAddedToLobbyClient(NUM_PLAYERS,NUM_PLAYERS)
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
      controllerActor ! PrivateLobbyCreatedClient("asdasdasd",NUM_PLAYERS)
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