import akka.actor._
import scala.concurrent.duration._
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy._

case class strings(msg: String)
case class startchild()

class ResumeException extends Exception
class RestartException extends Exception
class StopException extends Exception

class child extends Actor {
	println("child has started")
	override def preRestart(reason: Throwable, message: Option[Any]): Unit=
	{
		println("Yes child is restarting")
	}
	def receive = { 
		case strings(msg)  => {
			if (msg == "throw") {
				println("Welcome to child " + msg) 
				throw new StopException
			}
			else
			{
				println("Alive")
			}
			}
	}
}

class parent extends Actor {
	override val supervisorStrategy =
	 OneForOneStrategy(maxNrOfRetries = 10, withinTimeRange = 1 second) {
	 case _: ResumeException => Resume
	 case _: StopException => Stop
	 case _: RestartException => Restart
	 case _: Exception => Escalate
	}
	println("Parent started")
	def receive = { 
		case strings(msg)  => {
			if (msg == "Throw") {
				println("Parent : " + msg) 
				val ref = context.actorSelection("child")
				ref ! strings("throw")
			}
			else
			{
				println("Parent : " + msg) 
				val ref = context.actorSelection("child")
				ref ! strings("StillAlive")
			}
			}
		case startchild  => {
			context.actorOf(Props[child], "child")
		}
	}
	override def preRestart(reason: Throwable, message: Option[Any]): Unit=
	{
		println("Yes parent is restarting")
	}
}

object supervision extends App {

	val system = ActorSystem("Supervision")

	val parentactorref = system.actorOf(Props[parent], "parent")

	parentactorref ! startchild()

	parentactorref ! strings("Throw")

	parentactorref ! strings("AliveCheck")

	Thread.sleep(1000)

	system.terminate
}
