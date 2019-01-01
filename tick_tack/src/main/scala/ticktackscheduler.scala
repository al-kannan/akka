import akka.actor._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

case class strings(msg: String)

class child extends Actor {
	def receive = { 
		case strings(msg)  => {println("Child " + msg) }
		case _  => {println("None ")}
	}
}

object ticktackschedule extends App {

	val system = ActorSystem("ticktack")

	val actorref = system.actorOf(Props[child], "child")

	actorref ! strings("Message 1")

	//actorref ! "Foo from outside"

	system.scheduler.scheduleOnce(50 milliseconds, actorref, "Foo")

	Thread.sleep(1000)

	system.terminate
}
