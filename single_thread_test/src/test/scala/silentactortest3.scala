package testdriven

import org.scalatest.WordSpecLike
import org.scalatest.MustMatchers
import akka.testkit.{ TestActorRef, TestKit }
import akka.actor._

  class SilentActorTest3 extends TestKit(ActorSystem("testsystem"))
    with WordSpecLike
    with MustMatchers
    with StopSystemAfterAll {

    "A Silent Actor" must {

      "change internal state when it receives a message, multi" in {
        import SilentActor3._

        val silentActor = system.actorOf(Props[SilentActor3], "s3")
        silentActor ! SilentMessage("whisper1")
        silentActor ! SilentMessage("whisper2")
        silentActor ! GetState(testActor)
        expectMsg(Vector("whisper1", "whisper2"))
      }

    }

  }

  object SilentActor3 {
    case class SilentMessage(data: String)
    case class GetState(receiver: ActorRef)
  }

  class SilentActor3 extends Actor {
    import SilentActor3._
    var internalState = Vector[String]()

    def receive = {
      case SilentMessage(data) =>
        internalState = internalState :+ data
      case GetState(receiver) => receiver ! internalState
    }
  }

