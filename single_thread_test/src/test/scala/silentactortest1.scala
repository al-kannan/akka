package testdriven

import org.scalatest.{WordSpecLike, MustMatchers}
import akka.testkit.TestKit
import akka.actor._
import org.scalatest.{Suite, BeforeAndAfterAll}

trait StopSystemAfterAll extends BeforeAndAfterAll {
this: TestKit with Suite =>
override protected def afterAll() {
super.afterAll()
system.shutdown()
}
}

class SilentActorTest1 extends TestKit(ActorSystem("testsystem"))
  with WordSpecLike
  with MustMatchers
  with StopSystemAfterAll {
  "A Silent Actor" must {
    "change state when it receives a message, single threaded" ignore {
      //Write the test, first fail
      fail("not implemented yet")
    }
    "change state when it receives a message, multi-threaded" ignore {
      //Write the test, first fail
      fail("not implemented yet")
    }
  }
}
