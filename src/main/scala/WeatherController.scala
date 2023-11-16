import cats.data.EitherT
import com.google.inject.Inject
import com.twitter.finatra.http.Controller
import com.twitter.finatra.http.annotations.QueryParam
import com.twitter.util.Future
import nws.{Forecast, NationalWeatherService}
import util.FutureMonad.twitterFutureMonad

class WeatherController @Inject()(nationalWeatherService: NationalWeatherService) extends Controller {
  prefix("/v1"){
    get("/forecast") { request: ForecastRequest =>
      val result = for {
        forecastUrl <- EitherT(nationalWeatherService.getGridForecastUrl(request.lat, request.lon))
          .leftMap(error => response.internalServerError.json(Map("error" -> error)))
        forecast <- EitherT(nationalWeatherService.getForecast(forecastUrl))
          .leftMap(error => response.internalServerError.json(Map("error" -> error)))
      } yield {
        val jsonResponse = Map(
          "temp_characterization" -> forecast.tempCharacterization,
          "short_forecast" -> forecast.shortForecast
        )
        response.ok.json(jsonResponse)
      }

      result.value.flatMap {
        case Right(successResponse) => Future.value(successResponse)
        case Left(errorResponse) => Future.value(errorResponse)
      }
    }
  }
}

case class ForecastRequest(@QueryParam lat: Double, @QueryParam lon: Double)
