package nws

import util.FutureMonad.twitterFutureMonad
import com.google.inject.Inject
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Request, Response}
import com.twitter.finatra.http.request.RequestBuilder.get
import com.twitter.util.Future
import io.circe.Json
import cats.implicits._
import cats.data.EitherT
import io.circe.parser._

class NationalWeatherService @Inject()(httpClient: Service[Request, Response]) {
  def getGridForecastUrl(lat: Double, lon: Double): Future[Either[String, String]] = {
    val requestString = s"https://api.weather.gov/points/${lat.toString},${lon.toString}"
    val request = get(requestString)
    request.headerMap.add("User-Agent", "(test-weather-app-8888111, en0lc7er4@mozmail.com)")
    request.headerMap.add("Accept", "application/json")
    val response = httpClient(request)
    response.flatMap { resp =>
      Future.value {
        parse(resp.contentString)
          .leftMap(_.message)
          .flatMap(_.hcursor.downField("properties").get[String]("forecast").leftMap(_.message)) match {
          case Right(forecast) => Right(forecast)
          case Left(error) => Left(s"Error grabbing forecast url: $error")
        }
      }
    }
  }

  def getForecast(forecastUrl: String): Future[Either[String, Forecast]] = {
    val request = get(forecastUrl)
    request.headerMap.add("User-Agent", "(test-weather-app-8888111, en0lc7er4@mozmail.com)")
    request.headerMap.add("Accept", "application/json")

    EitherT(httpClient(request)
      .map(Right(_))
      .handle {
        case e: Exception => Left(s"Error making forecast request: ${e.getMessage}")
      }
    ).flatMapF {
      response =>
        parse(response.contentString).leftMap(_.message) match {
          case Right(json) =>
            val maybeForecastData = for {
              firstPeriodJson <- json.hcursor.downField("properties").downField("periods").as[List[Json]].toOption.flatMap(_.headOption)
              temp <- firstPeriodJson.hcursor.get[Int]("temperature").toOption
              unit <- firstPeriodJson.hcursor.get[String]("temperatureUnit").toOption
              shortForecast <- firstPeriodJson.hcursor.get[String]("shortForecast").toOption
            } yield Forecast(characterizeTemp(temp, unit), shortForecast)
            Future.value(maybeForecastData.toRight("No forecast data available"))

          case Left(error) => Future.value(Left(s"Error forming forecast data: $error"))
        }
    }.value
  }


  def characterizeTemp(temp: Int, unit: String): String = {
    var scaledTemp: Int = temp
    if (unit == "F") {
      scaledTemp = (temp - 32) * 5 / 9
    }
    if (scaledTemp > 22) "Hot" else if (scaledTemp < 9) "Cold" else "Moderate"
  }
}

case class Forecast(tempCharacterization: String, shortForecast: String)