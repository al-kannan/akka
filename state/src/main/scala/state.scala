import akka.actor._

case object current_state

class state extends Actor {
	var state_counter = 0
	println("While starting " + state_counter)
	def receive = { 
		case current_state  => println("Current State " + state_counter) 
		state_counter += 1
		}
}

object state extends App {

	val system = ActorSystem("state")

	val actorref1 = system.actorOf(Props[state], "state1")

	actorref1 ! current_state
	actorref1 ! current_state
	actorref1 ! current_state
	actorref1 ! current_state
	actorref1 ! current_state

	val actorref2 = system.actorOf(Props[state], "state2")
	actorref2 ! current_state
	actorref2 ! current_state

	actorref1 ! PoisonPill
	actorref2 ! PoisonPill

	system.terminate
}
