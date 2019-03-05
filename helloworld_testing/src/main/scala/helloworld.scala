import akka.actor._
case class hello(Msg: String)

class hellowworldactor extends Actor with ActorLogging {
	def receive = { 
		case hello(msg)  => println("hello case class") 
		case _ => println("Hello World from non hello case class") 
		}
}

object helloworld extends App {
	val system = ActorSystem("actorsystem")

	val actorref = system.actorOf(Props[hellowworldactor], "actor")

	actorref ! hello("Testing")

	actorref ! "Testing"

	Thread.sleep(5000)

	system.terminate
}
