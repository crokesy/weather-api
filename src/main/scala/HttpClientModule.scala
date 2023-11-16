import com.google.inject.{Provides, Singleton}
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.inject.TwitterModule

object HttpClientModule extends TwitterModule {
  @Singleton
  @Provides
  def providesHttpClient(): Service[Request, Response] = {
    HttpClientSingleton.client
  }
}