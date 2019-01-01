import akka.actor._

case class hello(Msg: String)
case class hallo(Msg: String)

class hellowworldactor extends Actor with ActorLogging {
	def receive = { 
		case hello(msg)  => println("hello") 
		case hallo(msg)  => println("hallo") 
		case _ => println("Hello World from Actor") 
		}
}

object helloworld extends App {

	println("Hello World Before Actor System")
	val system = ActorSystem("hellowworld")
	println("Hello World After Actor System")

	val actorref = system.actorOf(Props[hellowworldactor], "hellowworldactor")

	println("Hello World After Starting the Actor")

	actorref ! "Test"
	actorref ! "Test2"

	println("Hello World After Sending Actor Message ")

	//Second Actor System 
	println("Hello World Before Actor System 2")
	val system2 = ActorSystem("hellowworld")
	println("Hello World After Actor System 2")
	val actorref2 = system2.actorOf(Props[hellowworldactor], "hellowworldactor")
	actorref2 ! "Test"

	actorref ! PoisonPill
	actorref2 ! PoisonPill

	system.terminate
	system2.terminate
}
