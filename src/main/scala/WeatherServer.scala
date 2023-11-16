import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.routing.HttpRouter

object WeatherServerMain extends WeatherServer

class WeatherServer extends HttpServer {
  override val modules: Seq[HttpClientModule.type] = Seq(HttpClientModule)

  override def configureHttp(router: HttpRouter): Unit = router.add[WeatherController]
}
