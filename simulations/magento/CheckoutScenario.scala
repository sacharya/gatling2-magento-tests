package magento
import io.gatling.core.Predef._
import io.gatling.http.Predef._
import io.gatling.jdbc.Predef._
import io.gatling.http.Headers.Names._
import scala.concurrent.duration._
import bootstrap._
import assertions._
import Headers._

object scenarioFactory {

    def createUserScenario(webhead: String): io.gatling.core.structure.ScenarioBuilder = {
        val homepage =
            exec(http("Get Homepage")
                    .get(webhead + "/")
                    .headers(headers_1)
                )

            val categories =
            exec(http("Get Catagories Page")
                    .get(webhead + "/index.php/electronics/computers/laptops.html")
                    .headers(headers_1)
                )

            val add_to_cart =
            exec(http("Get Product Page")
                    .get(webhead + "/index.php/electronics/computers/laptops/apple-macbook-pro-ma464ll-a-15-4-notebook-pc-2-0-ghz-intel-core-duo-1-gb-ram-100-gb-hard-drive-superdrive.html")
                    .headers(headers_1)
                )
            .exec(http("Add To Cart")
                    .post(webhead + "/index.php/checkout/cart/add/uenc/aHR0cDovLzE5Mi4yMzcuMjIyLjg1L21hZ2VudG8vaW5kZXgucGhwL2VsZWN0cm9uaWNzL2NvbXB1dGVycy9sYXB0b3BzL2FwcGxlLW1hY2Jvb2stcHJvLW1hNDY0bGwtYS0xNS00LW5vdGVib29rLXBjLTItMC1naHotaW50ZWwtY29yZS1kdW8tMS1nYi1yYW0tMTAwLWdiLWhhcmQtZHJpdmUtc3VwZXJkcml2ZS5odG1sP19fX1NJRD1V/product/25/")
                    .headers(headers_73)
                    .param("""product""", """25""")
                    .param("""related_product""", """""")
                    .param("""qty""", """1""")
                    .check(status.is(302))
                 )

            val checkout =
            exec(http("Proceed to Checkout")
                    .get(webhead + "/index.php/checkout/onepage/")
                    .headers(headers_checkout_onepage)
                    .check(status.is(302))
                )
            .exec(http("Checkout as Guest")
                    .post(webhead + "/index.php/checkout/onepage/saveMethod")
                    .headers(headers_savemethod)
                    .param("""method""", """guest""")
                    .check(status.is(403))
                 )
            .exec(http("Load Billing")
                    .get(webhead + "/index.php/checkout/onepage/progress?prevStep=billing")
                    .headers(headers_2)
                    .check(status.is(403))
                 )
            .feed(csv("buyers.csv").queue)
            .exec(http("Submit Billing")
                    .post(webhead + "/checkout/onepage/saveBilling/")
                    .headers(headers_savebilling)
                    .param("""billing[address_id]""", """""")
                    .param("""billing[firstname]""", """Test""")
                    .param("""billing[lastname]""", "${Lastname}")
                    .param("""billing[company]""", """""")
                    .param("""billing[email]""", "${Email}")
                    .param("""billing[street][]""", """123 Any St""")
                    .param("""billing[street][]""", """""")
                    .param("""billing[city]""", """Dearborn""")
                    .param("""billing[region_id]""", """33""")
                    .param("""billing[region]""", """""")
                    .param("""billing[postcode]""", """48124""")
                    .param("""billing[country_id]""", """US""")
                    .param("""billing[telephone]""", """734-555-1234""")
                    .param("""billing[fax]""", """""")
                    .param("""billing[customer_password]""", """""")
                    .param("""billing[confirm_password]""", """""")
                    .param("""billing[save_in_address_book]""", """1""")
                    .param("""billing[use_for_shipping]""", """1""")
                    .check(status.is(403))
                    )
                    .exec(http("Get Additional")
                            .post(webhead + "/checkout/onepage/getAdditional/")
                            .headers(headers_getadditional)
                            .check(status.is(200))
                         )
                    .exec(http("Load Shipping")
                            .get(webhead + "/checkout/onepage/progress/")
                            .headers(headers_getshipping)
                            .queryParam("""toStep""", """shipping_method""")
                            .check(status.is(403))
                         )
                    .exec(http("Submit Shipping")
                            .post(webhead + "/checkout/onepage/saveShippingMethod/")
                            .headers(headers_saveshipping)
                            .param("""shipping_method""", """flatrate_flatrate""")
                            .param("""giftoptions[7][type]""", """quote""")
                            .param("""giftoptions[10][type]""", """quote_item""")
                            .check(status.is(403))
                         )
                    .exec(http("Load Payment")
                            .get(webhead + "/checkout/onepage/progress/")
                            .headers(headers_getpayment)
                            .queryParam("""toStep""", """payment""")
                            .check(status.is(403))
                         )
                    .exec(http("Submit Payment")
                            .post(webhead + "/checkout/onepage/savePayment/")
                            .headers(headers_savepayment)
                            .param("""payment[method]""", """checkmo""")
                            .check(status.is(403))
                         )
                    .exec(http("Load Review")
                            .get(webhead + "/checkout/onepage/progress/")
                            .headers(headers_getreview)
                            .queryParam("""toStep""", """review""")
                            .check(status.is(403))
                         )
                    .exec(http("Submit Order")
                            .post(webhead + "/checkout/onepage/saveOrder/")
                            .headers(headers_savereview)
                            .param("""payment[method]""", """checkmo""")
                            .check(status.is(302))
                         )
                    .exec(http("Load Success")
                            .get(webhead + "/checkout/onepage/success/")
                            .headers(headers_1)
                            .check(status.is(302))
                         )
                    .exec(http("Get Homepage")
                            .get(webhead + "/")
                            .headers(headers_1)
                            .check(status.is(200))
                         )

                    val scn = scenario("Anonymous Checkout")
                    .exec(homepage , categories, add_to_cart, checkout )
                    
        return scn
    }
}