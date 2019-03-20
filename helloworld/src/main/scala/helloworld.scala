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

	val system = ActorSystem("mysystem")

	val actorref = system.actorOf(Props[hellowworldactor], "mysystem")

	actorref ! hello("Test")

	actorref ! hallo("Test")

	actorref ! PoisonPill

	system.terminate

}
