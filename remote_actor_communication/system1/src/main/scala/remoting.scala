import akka.actor.{Actor, ActorRef, ActorSystem, Props }
import com.typesafe.config.ConfigFactory

object RemoteActor {
case class Work(message: String)
}

class RemoteActor extends Actor {
import RemoteActor._

	def receive = {
		case msg: Work  => {
			println("remote received " + msg + " from " + sender)
			println(msg)
			println(s"I received Work message and my ActorRef : ${self}")
			sender ! "Received Your Message: " + msg
		}
	}
}


object system1 extends App {
import RemoteActor._

	val config = ConfigFactory.load.getConfig("system1")

	val system = ActorSystem("system1", config)

	val remoteactor = system.actorOf(Props[RemoteActor], name="RemoteActor")

	println(s"Worker actor path is ${remoteactor.path}")

}
