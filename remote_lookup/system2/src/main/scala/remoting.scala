import akka.actor.{Actor, ActorRef, ActorSystem, Props }
import com.typesafe.config.ConfigFactory

object RemoteActor {
case class Work(message: String)
}


class RemoteActor extends Actor {
import RemoteActor._
	val remoteactor = context.actorSelection("akka.tcp://system1@172.31.26.214:2552/user/RemoteActor")
	def receive = {
		case msg: Work => {
			println("Sending the message")
			remoteactor ! Work("This message is from system2")
			}
		case msg: String => {
			println(msg);
		}
	}
}


object system2 extends App {
import RemoteActor._

	val config = ConfigFactory.load.getConfig("system2")

	val system = ActorSystem("system2", config)

	val actorref = system.actorOf(Props[RemoteActor], name="RemoteActor")

	actorref ! "Testing"

	actorref ! Work("This is from second actor system")
}
