import akka.actor.{ActorSystem, Props}
import akka.actor.LoggingFSM

object Event1
object Event2
object NewEvent

sealed trait ActorStatus
case object Locked extends ActorStatus
case object Unlocked extends ActorStatus

case class Data(total: Int) {
  def addOne = Data(total + 1)
}

object Data {
  def noCoin = Data(0)
}

class Actor extends LoggingFSM[ActorStatus, Data] {
  println("Actor Initialization commands goes hear")
  startWith(Locked, Data.noCoin)

  when(Locked) {
    case Event(Event2, _) => goto(Unlocked).using(stateData.addOne)
    case Event(Event1, _) => {
      println(s"Seriously? You can't ! I'm currently $stateName.")
      stay
    }
  }

  when(Unlocked) {
    case Event(NewEvent, _) => { 
    	println(s"New Event from $stateName state") 
	//goto(Locked)
	stay
	}
    case _ => throw new NotImplementedError()
  }

  initialize()
}

object program extends App {
    val system = ActorSystem("TurnstileSimulator")
    val turnstileActor = system.actorOf(Props(classOf[Actor]), "Turnstile")
    turnstileActor ! Event1 //Locked State
    turnstileActor ! NewEvent //Locked State with no handler
    turnstileActor ! Event2 //UnLocked State

    turnstileActor ! NewEvent //UnLocked State
    turnstileActor ! Event1 //UnLocked State, this should throw an exception

    Thread.sleep(5000)
    system.terminate
}

