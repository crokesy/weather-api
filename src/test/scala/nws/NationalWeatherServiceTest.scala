package nws

import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.util.{Await, Future}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{mock, when}
import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.must.Matchers
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class NationalWeatherServiceTest extends AsyncFlatSpec with Matchers {
  val mockHttpClient: Service[Request, Response] = mock(classOf[Service[Request, Response]])
  val nationalWeatherService = new NationalWeatherService(mockHttpClient)

  def setUpGeneralMockData(jsonValue: String): Unit = {
    val mockResponse = mock(classOf[Response])
    when(mockHttpClient.apply(any[Request])).thenReturn(Future.value(mockResponse))
    when(mockResponse.contentString).thenReturn(jsonValue)
  }

  "getGridForecastUrl" should "return the correct forecast URL on successful response" in {
    setUpGeneralMockData("""{"properties": {"forecast": "https://api.weather.gov/forecast"}}""")

    val result = Await.result(nationalWeatherService.getGridForecastUrl(39.7456, -97.0892))
    result shouldBe Right("https://api.weather.gov/forecast")
  }

  "getGridForecastUrl" should "handle a json error properly" in {
    setUpGeneralMockData("""{"properties": {}""")

    val result = Await.result(nationalWeatherService.getGridForecastUrl(39.7456, -97.0892))
    result shouldBe Left("Error grabbing forecast url: exhausted input")
  }

  "getForecast" should "return a Forecast object on successful request" in {
    setUpGeneralMockData("""{"properties": {"periods": [{"temperature": 20, "temperatureUnit": "F", "shortForecast": "Clear"}]}}""")

    val result = Await.result(nationalWeatherService.getForecast("example.forecast.url"))
    result shouldBe Right(Forecast("Cold", "Clear"))
  }

  "getForecast" should "return an informative message on receiving no forecast data" in {
    setUpGeneralMockData("""{"properties": {"periods": [{}]}}""")

    val result = Await.result(nationalWeatherService.getForecast("example.forecast.url"))
    result shouldBe Left("No forecast data available")
  }
}