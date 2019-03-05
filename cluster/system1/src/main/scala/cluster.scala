import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory
import akka.cluster.Member
import akka.actor.{ PoisonPill, ActorLogging, Actor, ActorRef, ActorSystem, Props, RootActorPath }
import scala.concurrent.duration._
import scala.collection.mutable.Map
import scala.io.Source
import java.io.File
import akka.util.Timeout
import scala.concurrent.{Await, ExecutionContext, Future}
import akka.pattern.ask



case class process_tx (Msg: String)
class StoreItemStockKeeper extends Actor with ActorLogging {
  def receive = {
	case process_tx(msg) => log.info("Process Tx : {} ", msg)
	case _ => log.info("Message Unknown")
	}
}

case class ready_ack (Msg: String)
case class launch_sk_actor (Msg: String)
class NodeCoordinator extends Actor with ActorLogging {
  def receive = {
  	case ready_ack(msg) => log.info("Yes Ready")
	case launch_sk_actor(msg) => 
		{	
		log.info("Stock Keeper Actor : {} ", msg)
		val ref = context.actorOf(Props[StoreItemStockKeeper], msg)
		sender ! ref
		}
	case _ => log.info("Message Unknown")
	}
}
	

case class file_check (Msg: String)
class ReceptionistActor extends Actor with ActorLogging {

  import context.dispatcher

  val cluster = Cluster(context.system)
  var node_seq = IndexedSeq.empty[Member]
  var actor_map = Map.empty[String, ActorRef]

  // subscribe to cluster changes, re-subscribe when restart
  override def preStart(): Unit = {
    cluster.subscribe(self, initialStateMode = InitialStateAsEvents, classOf[MemberEvent], classOf[UnreachableMember])
  }
  override def postStop(): Unit = cluster.unsubscribe(self)

  context.system.scheduler.schedule(20.seconds, 20.seconds, self, file_check("Check for New File"))

  def receive = {
    case MemberUp(member) =>
      {
      log.info("Member is Up: {}", member.address)
      node_seq = node_seq :+ member
      for (num <- 0 to node_seq.length-1) {
      	log.info("Node Address : {} ", node_seq(num).address) 
	context.actorSelection(RootActorPath(node_seq(num).address) / "user" / "NodeCoordinator") ! ready_ack("Testing") 
	}
      }
    case MemberRemoved(member, previousStatus) =>
      {
      log.info( "Member is Removed: {} after {}", member.address, previousStatus)
      node_seq = node_seq.filterNot(_ == member)
      }

    case file_check(msg) =>  
	{	
	val files = getListOfFiles(new File("/home/ec2-user/sbt_home/AKKA/cluster/system1"), List("file"))
        if (files.isEmpty) 
		log.info("No files to process")
        else
		{
		for (name <- files) {
			actor_map = Map.empty[String, ActorRef]
			log.info("Processing File : {} ", name)
			val node_count = node_seq.size
			log.info("Node Count :{}", node_count)
			var node_counter = 0
			for (line <- Source.fromFile(name).getLines) {
				if (actor_map.contains(line.substring(1,20)))
					actor_map(line.substring(1,20)) ! process_tx(line)
				else 	
					{
					log.info("Node Counter :{}", node_counter)
					log.info("Processing :{}", line.substring(1,20))
					log.info("Launching Node {} : ", node_seq(node_counter).address)
					implicit val timeout = Timeout(5 seconds)
					val future = context.actorSelection(RootActorPath(node_seq(node_counter).address) / "user" / "NodeCoordinator") ? 
						launch_sk_actor (line.substring(1,20))
					val result = Await.result(future, timeout.duration).asInstanceOf[ActorRef]
					log.info("ActorRef : {} ", result)
					actor_map += (line.substring(1,20) -> result)
					actor_map(line.substring(1,20)) ! process_tx(line)
					if ((node_counter+1) <= (node_count-1)) node_counter += 1
					else node_counter = 0
					}
				}
			actor_map foreach {case (key, value) => value ! PoisonPill}
			}
		}
	}
    case _: MemberEvent => // ignore
  }

  def getListOfFiles(dir: File, extensions: List[String]): List[File] = {
    dir.listFiles.filter(_.isFile).toList.filter { file =>
        extensions.exists(file.getName.endsWith(_))
    }
}

}


object ClusterSystem extends App {

	val system = ActorSystem("ClusterSystem")

	val nc = system.actorOf(Props[NodeCoordinator], "NodeCoordinator")

	val clusteractor = system.actorOf(Props[ReceptionistActor], "ReceptionistActor")

	println(s"Worker actor path is ${clusteractor.path}")
}
