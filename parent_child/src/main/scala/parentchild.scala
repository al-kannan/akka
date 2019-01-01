import akka.actor._

case class create_subchild()
case class strings(msg: String)

class child extends Actor {
	def receive = { 
		case create_subchild() => {
			println("Create Sub child")
			val ref = context.actorOf(Props[subchild], "subchild")
			ref ! strings("Test 1")
			}
		case strings(msg)  => {println("Welcome to Child " + msg) }
	}
}

class subchild extends Actor {
	def receive = { 
		case strings(msg)  => {println("Welcome to Sub Child " + msg) }
}
}

object state extends App {

	val system = ActorSystem("parentchild")

	val actorref = system.actorOf(Props[child], "child")

	actorref ! strings("Test 0")

	actorref ! create_subchild()

	var sel = system.actorSelection("akka://parentchild/user/*")
	sel ! strings("Test 2")

	sel = system.actorSelection("akka://parentchild/user/child/*")
	sel ! strings("Test 2")

	actorref ! PoisonPill

	// Sending to sub child after child been killed
	sel ! strings("Test 3")

	// Sending to child after PoisonPill
	actorref ! strings("Test 4")

	system.terminate
}
