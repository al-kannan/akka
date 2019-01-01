import akka.actor._

case class hello(Msg: String)
case class relativepath(Msg: String)

class refpathselection extends Actor {
	def receive = { 
		case hello(msg)  => { 
			println(msg)  
			}
		case relativepath(msg) => {
			println(msg)
			//Relative Path
			var sel = context.actorSelection("../refpathselection2") 
			sel ! hello("Testing Actor Selection relative path..called from actor 1")
			}
		case _ => {
			println(akka.serialization.Serialization.serializedActorPath(self))
			}
		}
}

object refpathselection extends App {

	val system = ActorSystem("refpathselection")

	//Actor Ref 
	val actorref = system.actorOf(Props[refpathselection], "refpathselection")
	actorref ! hello("Testing Actorref")


	//Absolute Path
	var sel = system.actorSelection("akka://refpathselection/user/refpathselection") 
	sel ! hello("Testing Actor Selection absolute path")

	system.actorOf(Props[refpathselection], "refpathselection2")

	actorref ! relativepath("Some")

	actorref ! PoisonPill

	system.terminate
}
