import com.twitter.finagle.{Http, Service}
import com.twitter.finagle.http.{Request, Response}

import javax.inject.Singleton

@Singleton
object HttpClientSingleton {
  lazy val client: Service[Request, Response]
  = Http.client.withTls("api.weather.gov").newService("api.weather.gov:443")
}