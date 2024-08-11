import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import java.net.HttpURLConnection
import java.net.URL

@Serializable
data class WeatherResponse(val list: List<WeatherData>)

@Serializable
data class WeatherData(
    val dt_txt: String,
    val main: MainData,
    val weather: List<WeatherDescription>
)

@Serializable
data class MainData(val temp: Double)

@Serializable
data class WeatherDescription(val description: String)

suspend fun fetchWeatherData(apiKey: String, postalCode: String) {
    val url = "http://api.openweathermap.org/data/2.5/forecast?zip=$postalCode,ch&units=metric&appid=$apiKey"
    val response = makeApiRequest(url)

    val weatherResponse = Json.decodeFromString<WeatherResponse>(response)
    println("Wettervorhersage für PLZ: $postalCode")

    for (data in weatherResponse.list) {
        println("${data.dt_txt}: ${data.main.temp}°C, ${data.weather[0].description}")
    }
}

fun makeApiRequest(apiUrl: String): String {
    val url = URL(apiUrl)
    val connection = url.openConnection() as HttpURLConnection

    return try {
        connection.inputStream.bufferedReader().readText()
    } finally {
        connection.disconnect()
    }
}

fun main() = runBlocking {
    print("Gib deine Schweizer PLZ ein: ")
    val postalCode = readLine() ?: ""

    val apiKey = "566cae7eb51b5f6ff38fa0f5138f7f4e"

    if (postalCode.matches(Regex("\\d{4}"))) {
        fetchWeatherData(apiKey, postalCode)
    } else {
        println("Ungültige PLZ. Bitte gib eine vierstellige Schweizer PLZ ein.")
    }
}
