package testdriven

import org.scalatest.WordSpecLike
import org.scalatest.MustMatchers
import akka.testkit.{ TestActorRef, TestKit }
import akka.actor._

class SilentActorTest2 extends TestKit(ActorSystem("testsystem"))
    with WordSpecLike
    with MustMatchers
    with StopSystemAfterAll {

    "A Silent Actor" must {

      "change internal state when it receives a message, single" in {
        import SilentActor2._

        val silentActor = TestActorRef[SilentActor2]
        silentActor ! SilentMessage("whisper")
        silentActor.underlyingActor.state must (contain("whisper"))
      }

    }
  }


  object SilentActor2 {
    case class SilentMessage(data: String)
    case class GetState(receiver: ActorRef)
  }

  class SilentActor2 extends Actor {
    import SilentActor2._
    var internalState = Vector[String]()

    def receive = {
      case SilentMessage(data) =>
        internalState = internalState :+ data
    }

    def state = internalState
  }

