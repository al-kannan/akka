import akka.actor._
import scala.concurrent.duration._

case object Tick
case object StartTimerTask

class tickchild extends Actor with Timers {
	def receive = { 
		case StartTimerTask => 
			println("From FirstTick")
			val uuid = java.util.UUID.randomUUID
			timers.startPeriodicTimer(uuid, Tick, 1.seconds)
		case Tick => 
			println("From Tick")
	}
}

object ticktack extends App {

	val system = ActorSystem("ticktack")

	val actorref = system.actorOf(Props[tickchild], "tickchild")

	actorref ! StartTimerTask

	Thread.sleep(3000)

	actorref ! PoisonPill

	Thread.sleep(1000)

	system.terminate
}
