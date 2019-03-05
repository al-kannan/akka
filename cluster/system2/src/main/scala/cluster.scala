import akka.cluster.Cluster
import akka.cluster.ClusterEvent._
import com.typesafe.config.ConfigFactory
import akka.actor.{ ActorLogging, Actor, ActorRef, ActorSystem, Props, RootActorPath }

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
                log.info("Stock Keeper Actor : {} ", msg)
                val ref = context.actorOf(Props[StoreItemStockKeeper], msg)
                sender ! ref
        case _ => log.info("Message Unknown")
        }
}


object system1 extends App {

        val system = ActorSystem("ClusterSystem")

        println("Second system started")

        val nc = system.actorOf(Props[NodeCoordinator], "NodeCoordinator")
}

