## Setup/Running

If you have docker installed you can run the following commands

- `docker build -t hunt-weather-service .`
- `docker run -p 8888:8888 weather-service` (feel free to update port mapping if this is already taken)

Otherwise, make sure you have scala (2.13.8) and sbt (1.9.7) installed and then you can set everything up with sbt:

- `sbt compile`
- `sbt test` (Optional)
- `sbt run`

## Hitting the API

Assuming the service is up and running then you can use the following command to verify the api responds properly

- `curl "localhost:8888/v1/forecast?lat=39.7456&lon=-97.0892"`
- the `/forecast` endpoint requires two query params, "lat" and "lon" for latitude and longitude

## Shortcuts

#### Testing

- I was able to include some tests, though the coverage and value is suspect. I'd like to add some feature
  tests that can cover the controller behavior as a whole

#### Caching

- I thought about but balked on providing some method of caching (like redis to help obviate the need for the second NWS
  call at the least) since the national weather service is a free tool and shouldn't be abused. I also couldn't fit that
  into the given timeframe either :)

#### Authentication

- Felt weird about providing some sort of auth since the endpoints being leveraged aren't walled to begin with, but
  might be something to consider for the future

#### Code

- the methods that grab the data from the NWS ideally would be decomposed more. Jamming all the logic into just those
  two felt/feels a little cheap.