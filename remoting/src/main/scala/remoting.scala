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
			//sender ! "hi"
		}
	}
}


object FirstRemoteSystem extends App {
import RemoteActor._

	val config = ConfigFactory.load.getConfig("FirstRemoteSystemConfiguration")

	val system = ActorSystem("MyFirstRemoteSystem", config)

	val remoteactor = system.actorOf(Props[RemoteActor], name="RemoteActor")

	println(s"Worker actor path is ${remoteactor.path}")

}

object SecondRemoteSystem extends App {
import RemoteActor._

	val config = ConfigFactory.load.getConfig("SecondRemoteSystemConfiguration")

	val system = ActorSystem("MySecondRemoteSystem", config)

	val remoteactor = system.actorSelection("akka.tcp://MyFirstRemoteSystem@172.31.26.214:2552/user/RemoteActor")

	remoteactor ! Work("This is from second actor system")
}
