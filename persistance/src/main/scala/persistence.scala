import akka.actor._

case object current_state
case object throw_error
case object shut_down

case class Evt(data: Int)
case class Cmd(data: String)

class state extends PersistentActor {
	var state_counter = 0
	println("While starting " + state_counter)
	override def persistenceId = "demo-persistance-actor-1"

	def updateState(event: Evt): Unit =
	    state_counter = state_counter + event;

	val receiveRecover: Receive = {
	    case evt: Evt => updateState(evt)
	    case SnapshotOffer(_, snapshot: Int) => {
		println(s"offered state = $snapshot")
		state_counter = snapshot
	    }
	  }

	val receiveCommand: Receive = {
	    case Cmd(data) =>
	      persist(Evt(data))(updateState)
	      persist(Evt(s"${data}-${numEvents + 1}")) { event =>
		updateState(event)
		context.system.eventStream.publish(event)
	      }
	    case "snap"  => saveSnapshot(state)
	    case SaveSnapshotSuccess(metadata) =>
	      println(s"SaveSnapshotSuccess(metadata) :  metadata=$metadata")
	    case SaveSnapshotFailure(metadata, reason) =>
	      println("""SaveSnapshotFailure(metadata, reason) :
		metadata=$metadata, reason=$reason""")
	    case "print" => println(state)
	    case "boom"  => throw new Exception("boom")
	  }


	def receive = { 
		case `current_state`  => 
			{
			println("Current State " + state_counter) 
			state_counter += 1
			}
		case `throw_error`  => 
			throw new Exception
		case `shut_down`  => 
			context.stop(self)
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

	actorref1 ! throw_error

	Thread.sleep(2000)

	actorref1 ! current_state

	actorref1 ! shut_down

	system.terminate
}
