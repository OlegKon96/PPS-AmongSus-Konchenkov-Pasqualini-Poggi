package model

import akka.actor.{ActorRef, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import it.amongsus.controller.ActionTimer.TimerStarted
import it.amongsus.controller.actor.ControllerActorMessages.{ButtonOffController, UpdatedMyCharController, UpdatedPlayersController}
import it.amongsus.controller.actor.{ControllerActor, LobbyActorInfo}
import it.amongsus.core.Drawable
import it.amongsus.core.map.Tile
import it.amongsus.core.player.CrewmateAlive
import it.amongsus.core.util.Movement.Up
import it.amongsus.core.util.Point2D
import it.amongsus.model.actor.ModelActorMessages.{KillTimerStatusModel, MyCharMovedModel}
import it.amongsus.model.actor.{ModelActor, ModelActorInfo}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class ModelActorTest extends TestKit(ActorSystem("test", ConfigFactory.load("test")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  private val modelActorInfo: ModelActorInfo = ModelActorInfo()
  private val map: Array[Array[Drawable[Tile]]] = modelActorInfo.generateMap(loadMap())
  modelActorInfo.generateCollectionables(map)
  val players = Seq(CrewmateAlive("green", emergencyCalled = false, "test", "test", 0, Point2D(35, 35)))

  "Get Ready, Win a Match and then Leave the Client" in {
    val controller = TestProbe()

    val modelActor = system.actorOf(ModelActor.props(ModelActorInfo(Option(controller.ref), Option(map), players,
      modelActorInfo.gameCollectionables, "test")))

    modelActor ! KillTimerStatusModel(TimerStarted)
    controller.expectMsgType[ButtonOffController]

    modelActor ! MyCharMovedModel(Up())
    controller.expectMsgType[ButtonOffController]
    controller.expectMsgType[ButtonOffController]
    controller.expectMsgType[ButtonOffController]
    controller.expectMsgType[ButtonOffController]
    controller.expectMsgType[UpdatedMyCharController]
    controller.expectMsgType[UpdatedPlayersController]

  }

  def loadMap(): Iterator[String] = {
    val bufferedSource = scala.io.Source.fromInputStream(getClass.getResourceAsStream("/images/gameMap.csv"))
    bufferedSource.getLines
  }
}
