import akka.actor.ActorSystem
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import java.util.Calendar
import java.util.concurrent.Executors

object futureexample extends App {
  def anyfunction(number: Int) = {
    // we sleep for 5 seconds and return number
    println("Started futureexample with " + number)
    println("Started at " + Calendar.getInstance().getTime())
    Thread.sleep(5000)
    number
  }

  var startTime = System.currentTimeMillis
  val number1 = anyfunction(1)
  val number2 = anyfunction(2)
  val number3 = anyfunction(3)
  var elapsedTime = ((System.currentTimeMillis - startTime) / 1000.0)
  println("Sum of 1, 2 and 3 is " + (number1+number2+number3) + " calculated in " + elapsedTime + " seconds")


  implicit val system = ActorSystem("future")
  startTime = System.currentTimeMillis
  val future1 = Future(anyfunction(4))
  val future2 = Future(anyfunction(5))
  val future3 = Future(anyfunction(6))

  println("After all future calls " + Calendar.getInstance().getTime())

  val future = for {
    x <- future1
    y <- future2
    z <- future3
  } yield (x + y + z)

  println("After for " + Calendar.getInstance().getTime())

  val f1result = Await.result(future1, 6 second)
  val f2result = Await.result(future2, 6 second)
  val f3result = Await.result(future3, 6 second)

  elapsedTime = ((System.currentTimeMillis - startTime) / 1000.0)
  println("Sum of 4, 5 and 6 is " + (f1result+f2result+f3result) + " calculated in " + elapsedTime + " seconds")

/*
  future onSuccess {
    case sum =>
      elapsedTime = ((System.currentTimeMillis - startTime) / 1000.0)
      println("Sum of 4, 5 and 6 is " + sum + " calculated in " + elapsedTime + " seconds")
  }
*/

}
