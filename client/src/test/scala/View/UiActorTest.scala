package View

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import it.amongsus.messages.LobbyMessagesClient._
import it.amongsus.view.actor.UiActorLobbyMessages._
import it.amongsus.view.actor.{UiActor, UiActorInfo}
import it.amongsus.view.frame.{LobbyFrame, MenuFrame}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class UiActorTest extends TestKit(ActorSystem("test", ConfigFactory.load("test")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)
  private val NUM_PLAYERS = 4

  "The UiActor" should {

    "Successfully connected to the server" in {
      val client = TestProbe()
      val menuFrame = MenuFrame.apply(Option(client.ref))
      val uiActor = system.actorOf(UiActor.props(UiActorInfo.apply(Option(client.ref), Option(menuFrame))))
      uiActor ! PublicGameSubmitUi("asdasdasd", NUM_PLAYERS)
      client.expectMsgType[JoinPublicLobbyClient]
    }

    "Accept into a private lobby connection with code" in {
      val client = TestProbe()
      val menuFrame = MenuFrame.apply(Option(client.ref))
      val uiActor =
        system.actorOf(UiActor.props(UiActorInfo.apply(Option(client.ref), Option(menuFrame))))
      uiActor ! PrivateGameSubmitUi("asdasdasd", "qwerty")
      client.expectMsgType[JoinPrivateLobbyClient]
    }

  }

}
