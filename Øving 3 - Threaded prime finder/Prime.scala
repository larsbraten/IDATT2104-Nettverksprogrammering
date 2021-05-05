import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration.Inf
import scala.concurrent.{Await, Future}
import scala.io.StdIn.readLine

/* Scala program which finds all primes in a given range with a given number of threads */
object Prime extends App {

  /* Start range  */
  var startRange = readLine("Enter the start of the range using only integers.\n").toInt
  /* End range  */
  var endRange = readLine("Enter the end of the range using only integers.\n").toInt
  /* Number of threads */
  var noThreads = readLine("How many threads should be used?\n").toInt
  /* Stopwatch to measure time spent finding primes */
  val time = System.currentTimeMillis()
  val primes = ((startRange to endRange groupBy (_ % noThreads))
    .values
    /* Starts an asynchronous computation and returns a Future instance (basically the scala version of threads)
     with the result of that computation */
    .map(slice => Future(slice.filter(findPrime))) flatMap (Await.result(_, Inf)))
    /* Makes a new list containing the results of the asynchronous computation and then immediately sorts it in ascending order */
    .toList .sorted
  val time2 = System.currentTimeMillis()
  /* Calculates time spent finding primes */
  val duration = time2 - time
  println("All primes found between " + startRange + " and " + endRange)
  primes
    /* Prints out the primes in rows of 50 and Tidies up the formatting */
    .grouped(50).foreach(primes => println(primes.mkString(", ")))

  /* Prints the result of the stopwatch */
  println("Time spent finding primes: " + duration + " milliseconds")
  /* Number of primes found by printing the length of the prime list */
  println("Number of primes: " + primes.length)

  /**
   * Checks whether a number is prime or not
   *
   * @param i The numbers in the given range
   * @return Boolean
   */
  /* This algorithm was taken from
  https://alvinalexander.com/scala/function-algorithm-determine-prime-numbers/
  Not Sieve of Eratosthenes, but it gets the job done.*/
  def findPrime(i: Int): Boolean =
  if (i <= 1) {
    false
  }
  else if (i equals 2) {
    true
  }
  else {
    !(2 until i).exists(n => i % n == 0)
  }
}
