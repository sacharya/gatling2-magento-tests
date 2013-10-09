package magento
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.Headers.Names._
import scala.concurrent.duration._
import assertions._

class CheckoutSimulation extends Simulation {
    val userOption: Int = Integer.getInteger("users", 1).toInt
    val timeOption: Int = Integer.getInteger("time", 1).toInt
    val defaultUrlBase = "http://127.0.0.1/magento"

    val webheads = System.getProperty("webheads", defaultUrlBase).split(",")

    println(webheads)
    val scns = webheads.map(scenarioFactory.createUserScenario(_).inject(ramp(userOption) over timeOption))
    println(scns)

    val httpProtocol = http
        .acceptCharsetHeader("ISO-8859-1,utf-8;q=0.7,*;q=0.7")
        .acceptHeader("text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
        .acceptEncodingHeader("gzip, deflate")
        .acceptLanguageHeader("en-us;q=0.9,en;q=0.3")
        .disableFollowRedirect

    setUp(scns(0), scns.drop(1):_*)
        .protocols(httpProtocol)
        .assertions(
            global.successfulRequests.percent.is(100),
            global.responseTime.mean.lessThan(1000))

}

