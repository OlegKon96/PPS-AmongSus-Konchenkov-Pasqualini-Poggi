package game

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestActorRef, TestKit, TestProbe}
import com.typesafe.config.ConfigFactory
import it.amongsus.messages.GameMessageClient.GamePlayersClient
import it.amongsus.messages.GameMessageServer.PlayerReadyServer
import it.amongsus.messages.LobbyMessagesServer.MatchFound
import it.amongsus.server.common.GamePlayer
import it.amongsus.server.game.GameActor
import it.amongsus.server.game.GameActor.GamePlayers
import org.scalamock.scalatest.MockFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class GameActorTest extends TestKit(ActorSystem("test", ConfigFactory.load("test")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll
  with MockFactory {

  override protected def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  private val NUMBER_OF_PLAYERS = 2

  "The game actor" should {

    "accept a specific numbers of players and notify that the game is started with an initial state" in {
      val gameActor = TestActorRef[GameActor](GameActor.props(NUMBER_OF_PLAYERS))
      val player1 = TestProbe()
      val player2 = TestProbe()

      gameActor ! GamePlayers(Seq(GamePlayer("id1", "player1", player1.ref), GamePlayer("id2", "player2", player2.ref)))
      player1.expectMsgType[MatchFound]
      player1.send(gameActor, PlayerReadyServer("id1", player1.ref))
      player2.expectMsgType[MatchFound]
      player2.send(gameActor, PlayerReadyServer("id2", player2.ref))

      player1.expectMsgType[GamePlayersClient]
      player2.expectMsgType[GamePlayersClient]
    }
  }
}