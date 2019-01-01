import akka.actor._

case object ForceRestart

class lifecycle extends Actor {
	var counter = 0
	println ("Creation Counter : " + counter)
	def receive = { 
		case ForceRestart => throw new Exception("Boom!")
		case _ => {
			println("Something else") 
			counter += 1
			}
	}

	override def preStart(): Unit = {println("preStart")
	println ("Counter Pre Start : " + counter)
	}
	override def postStop(): Unit = {println("postStop")
	println ("Counter : postStop " + counter)
	}
	override def preRestart(reason: Throwable, message: Option[Any]): Unit = {println("preRestart") 
	println ("Counter : preRestart " + counter)
	}
	override def postRestart(reason: Throwable): Unit = {println("postRestart") 
	println ("Counter : postRestart " + counter)
	}
}

object lifecycle extends App {
	val system = ActorSystem("lifecycle")

	val actorref = system.actorOf(Props[lifecycle], "lifecycle")

	actorref ! ""
	Thread.sleep(1000)

	actorref ! ForceRestart
	Thread.sleep(1000)

	actorref ! PoisonPill

	system.terminate
}
