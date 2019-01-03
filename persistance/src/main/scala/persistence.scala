import akka.actor._
import akka.persistence._

case class Evt(data: Int)
case class Cmd(data: Int)

class state extends PersistentActor {
	var state_counter = 0
	println("While starting " + state_counter)

	override def persistenceId = "demo-persistance-actor-1"

	def updateState(event: Evt): Unit =
	    state_counter = state_counter + event.data;

	val receiveRecover: Receive = {
	    case evt: Evt => 
	    	println("Recover value : " + evt.data)
	    	updateState(evt)
	    case SnapshotOffer(_, snapshot: Int) => {
		println(s"offered state = $snapshot")
		state_counter = snapshot
	    }
	  }

	val receiveCommand: Receive = {
	    case Cmd(data) =>
	      persist(Evt(data)) { event =>
		updateState(event)
		println("Stored Command Count : " + lastSequenceNr) 
	      }
	    case "snap"  => saveSnapshot(state_counter)
	    case SaveSnapshotSuccess(metadata) =>
	      println(s"SaveSnapshotSuccess(metadata) :  metadata=$metadata")
	    case SaveSnapshotFailure(metadata, reason) =>
	      println(s"SaveSnapshotFailure(metadata) :  metadata=$metadata reason=$reason")
	    case "print" => println(state_counter)
	    case "boom"  => throw new Exception("boom")
	  }
}

object state extends App {

	val system = ActorSystem("state")

	val actorref1 = system.actorOf(Props[state], "state1")

	actorref1 ! "print"

	actorref1 ! Cmd(1)

	actorref1 ! "print"

	actorref1 ! "boom"

	actorref1 ! "snap"

	Thread.sleep(2000)

	system.terminate
}
