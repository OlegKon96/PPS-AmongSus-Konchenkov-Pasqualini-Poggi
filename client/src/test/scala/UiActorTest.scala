import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import com.typesafe.config.ConfigFactory
import org.scalatest.BeforeAndAfterAll
import org.scalatest.wordspec.AnyWordSpecLike

class UiActorTest extends TestKit(ActorSystem("test", ConfigFactory.load("test")))
  with ImplicitSender
  with AnyWordSpecLike
  with BeforeAndAfterAll {

}
