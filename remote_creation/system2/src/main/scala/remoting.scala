import akka.actor.{Actor, ActorRef, ActorSystem, Props }
import com.typesafe.config.ConfigFactory

object RemoteActor {
	case class Work(message: String)
}

class RemoteActor extends Actor {
	import RemoteActor._
	println("RemoteActor got created : system2 version")

	def receive = {
		case msg: Work  => {
			println(msg)
			println(s"My ActorRef : ${self}")
		}
	}
}


object system2 extends App {
	val config = ConfigFactory.load.getConfig("system2")

	val system = ActorSystem("system2", config)
}
