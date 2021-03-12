package controller

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import it.amongsus.controller.ActionTimer.TimerStarted
import it.amongsus.controller.actor.ControllerActorMessages.{ActionOnController, BeginVotingController, GameEndController, KillTimerController, MyCharMovedController, PlayerLeftController, SendTextChatController, TestGameBehaviour, UpdatedPlayersController}
import it.amongsus.controller.actor.{ControllerActor, LobbyActorInfo, LobbyActorInfoData}
import it.amongsus.core.map.Tile
import it.amongsus.core.{Drawable, player}
import it.amongsus.core.player.{ImpostorAlive, Player}
import it.amongsus.core.util.ActionType.EmergencyAction
import it.amongsus.core.util.GameEnd.{CrewmateCrew, Win}
import it.amongsus.core.util.Direction.Up
import it.amongsus.core.util.{ChatMessage, Point2D}
import it.amongsus.messages.GameMessageClient._
import it.amongsus.messages.GameMessageServer.{LeaveGameServer, PlayerReadyServer, SendTextChatServer, StartVoting}
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.messages.LobbyMessagesServer._
import it.amongsus.model.actor.ModelActorMessages.{BeginVotingModel, InitModel, KillPlayerModel, MyCharMovedModel, MyPlayerLeftModel, PlayerMovedModel}
import it.amongsus.model.actor.{ModelActor, ModelActorInfo}
import it.amongsus.view.actor.{UiActor, UiActorInfo}
import it.amongsus.view.actor.UiActorGameMessages.{ActionOffUi, ActionOnUi, BeginVotingUi, GameEndUi, KillTimerUpdateUi, NoOneEliminatedUi, PlayerLeftUi, PlayerUpdatedUi, ReceiveTextChatUi, SabotageTimerUpdateUi}
import it.amongsus.view.actor.UiActorLobbyMessages._
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class ControllerActorTest extends TestKit(ActorSystem("test", ConfigFactory.load("test")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)
  private val NUM_PLAYERS = 4
  private final val positionDefault35 = 35
  private val crewmateAlive: Player = player.CrewmateAlive("green", emergencyCalled = false, "asdasdasd",
    "imCrewmate", 3, Point2D(positionDefault35, positionDefault35))
  private val modelActor: ModelActorInfo = ModelActorInfo()
  private val map: Array[Array[Drawable[Tile]]] = modelActor.generateMap(loadMap())
  modelActor.generateCollectionables(map)

  val client: TestProbe = TestProbe()
  val serverActor: TestProbe = TestProbe()
  val model: TestProbe = TestProbe()
  val uiActor: TestProbe = TestProbe()
  val controllerActor: ActorRef =
    system.actorOf(ControllerActor.props(LobbyActorInfo(Option(serverActor.ref), Option(uiActor.ref), "dasds")))

  val players = Seq(ImpostorAlive("green", emergencyCalled = false, "dasds", "asdasdsd", Point2D(0,0)))

  "The Controller Actor" should {

    "Start Game, Get Ready, Start Voting, Vote Player, Eliminate Player and End Game" in {
      controllerActor ! TestGameBehaviour(model.ref, serverActor.ref)
      serverActor.expectNoMessage()

      controllerActor ! PlayerReadyClient()
      serverActor.expectMsgType[PlayerReadyServer]

      controllerActor ! BeginVotingController(Seq())
      serverActor.expectMsgType[StartVoting]
      uiActor.expectMsgType[BeginVotingUi]

      controllerActor ! VoteClient("dasds")
      serverActor.expectMsgType[VoteClient]

      controllerActor ! EliminatedPlayer("dasds")
      model.expectMsgType[KillPlayerModel]
      uiActor.expectMsgType[EliminatedPlayer]

      controllerActor ! GameEndController(Win(Seq(),CrewmateCrew()))
      uiActor.expectMsgType[GameEndUi]

      uiActor.expectNoMessage()
      model.expectNoMessage()
      serverActor.expectNoMessage()
      client.expectNoMessage()
    }

    "Start Game, Get Ready, Move Player, Press Emergency, Send Text to Chat, No Voting, Player Left and Left Game" in {
      controllerActor ! TestGameBehaviour(model.ref, serverActor.ref)
      serverActor.expectNoMessage()

      controllerActor ! PlayerReadyClient()
      serverActor.expectMsgType[PlayerReadyServer]

      controllerActor ! MyCharMovedController(Up())
      model.expectMsgType[MyCharMovedModel]

      controllerActor ! PlayerMovedClient(crewmateAlive, Seq())
      model.expectMsgType[PlayerMovedModel]

      controllerActor ! UpdatedPlayersController(crewmateAlive, players, modelActor.gameCollectionables, Seq())
      uiActor.expectMsgType[PlayerUpdatedUi]

      controllerActor ! ActionOnController(EmergencyAction())
      uiActor.expectMsgType[ActionOnUi]

      controllerActor ! StartVotingClient(players)
      model.expectMsgType[BeginVotingModel]
      uiActor.expectMsgType[BeginVotingUi]

      controllerActor ! SendTextChatController(ChatMessage("dasds", "Hello"), crewmateAlive)
      serverActor.expectMsgType[SendTextChatServer]

      controllerActor ! SendTextChatClient(ChatMessage("dasds", "Hello"))
      uiActor.expectMsgType[ReceiveTextChatUi]

      controllerActor ! NoOneEliminatedController()
      uiActor.expectMsgType[NoOneEliminatedUi]

      controllerActor ! PlayerLeftClient("dasds")
      uiActor.expectMsgType[PlayerLeftUi]

      controllerActor ! PlayerLeftController()
      model.expectMsgType[MyPlayerLeftModel]

      uiActor.expectNoMessage()
      model.expectNoMessage()
      serverActor.expectNoMessage()
      client.expectNoMessage()
    }

    "Add a User to a Lobby Client" in {
      val controllerActor =
        system.actorOf(ControllerActor.props(LobbyActorInfo.apply(Option(client.ref))))
      controllerActor ! UserAddedToLobbyClient(NUM_PLAYERS,NUM_PLAYERS)
      client.expectMsgType[UserAddedToLobbyUi]
    }

    "Update the Lobby Client" in {
      val controllerActor =
        system.actorOf(ControllerActor.props(LobbyActorInfo.apply(Option(client.ref))))
      controllerActor ! UpdateLobbyClient(NUM_PLAYERS)
      client.expectMsgType[UpdateLobbyClient]
    }

    "Create a Private Lobby Client" in {
      val controllerActor =
        system.actorOf(ControllerActor.props(LobbyActorInfo.apply(Option(client.ref))))
      controllerActor ! PrivateLobbyCreatedClient("asdasdasd",NUM_PLAYERS)
      client.expectMsgType[PrivateLobbyCreatedUi]
    }

    "Match Found" in {
      val controllerActor =
        system.actorOf(ControllerActor.props(LobbyActorInfo.apply(Option(client.ref))))
      controllerActor ! MatchFound(client.ref)
      client.expectMsgType[MatchFoundUi]
    }
  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/images/gameMap.csv"))
    bufferedSource.getLines
  }
}