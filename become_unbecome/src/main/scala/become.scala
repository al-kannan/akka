import akka.actor._

case object Message1
case object Message2
case object Message3
case object Message4
case object Message5
case object NormalState
case object AbnormalState

class actor extends Actor {
	import context._
	println("Actor started")
	def normalstate: Receive = {
		case Message1 =>
			println("I am message 1")
			become(abnormalstate)
		case Message2 =>
			println("I am message 2")
	}
	def abnormalstate: Receive = {
		case Message3 =>
			println("I am message 3")
		case Message4 =>
			println("I am message 4")
			become(normalstate)
	}
	def receive = { 
		case NormalState  => become(normalstate)
		case AbnormalState  => become(abnormalstate)
	}
}

object becomeapp extends App {

	val system = ActorSystem("BecomeApp")

	val actorref = system.actorOf(Props[actor], "Actor")

	actorref ! NormalState

	actorref ! Message1
	actorref ! Message2

	actorref ! AbnormalState

	actorref ! Message3
	actorref ! Message4

	actorref ! NormalState

	actorref ! Message3
	actorref ! Message4

	actorref ! Message5

	Thread.sleep(2000)

	system.terminate
}
