package model

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import it.amongsus.controller.ActionTimer.TimerStarted
import it.amongsus.controller.actor.ControllerActorMessages._
import it.amongsus.core.Drawable
import it.amongsus.core.map.MapHelper.{GameMap, generateMap}
import it.amongsus.core.map.Tile
import it.amongsus.core.player.CrewmateAlive
import it.amongsus.core.util.ActionType.EmergencyAction
import it.amongsus.core.util.Direction.Up
import it.amongsus.core.util.Point2D
import it.amongsus.model.actor.ModelActorMessages._
import it.amongsus.model.actor.{ModelActor, ModelActorInfo}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class ModelActorTest extends TestKit(ActorSystem("test", ConfigFactory.load("test")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  private final val positionInTheMap: Int = 35
  private val modelActorInfo: ModelActorInfo = ModelActorInfo()
  private val map: GameMap = generateMap(loadMap())
  private val players = Seq(CrewmateAlive("green", emergencyCalled = false, "test", "test", 0, Point2D(positionInTheMap,
    positionInTheMap)))

  "Start Game, Check Timer, Moved Character, Call Emergency, Vote Player and Leave Game" in {
    val controller = TestProbe()
    val modelActor = system.actorOf(ModelActor.props(ModelActorInfo(Option(controller.ref), Option(map), players,
      modelActorInfo.gameCoins, "test")))

    modelActor ! KillTimerStatusModel(TimerStarted)
    controller.expectMsgType[ActionOffController]

    modelActor ! MyCharMovedModel(Up)
    controller.expectMsgType[ActionOffController]
    controller.expectMsgType[ActionOffController]
    controller.expectMsgType[ActionOffController]
    controller.expectMsgType[ActionOffController]
    controller.expectMsgType[UpdatedMyCharController]
    controller.expectMsgType[UpdatedPlayersController]

    modelActor ! UiActionModel(EmergencyAction)
    controller.expectMsgType[ActionOffController]
    controller.expectMsgType[ActionOffController]
    controller.expectMsgType[BeginVotingController]

    modelActor ! KillPlayerModel("test")
    controller.expectMsgType[UpdatedPlayersController]

    modelActor ! RestartGameModel
    modelActor ! MyPlayerLeftModel
    controller.expectNoMessage()
  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/images/gameMap.csv"))
    bufferedSource.getLines
  }
}