import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.aaaaa.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray

class MainActivity : AppCompatActivity() {

    private lateinit var cityEditText: EditText
    private lateinit var weatherTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        cityEditText = findViewById(R.id.cityEditText)
        weatherTextView = findViewById(R.id.weatherTextView)
    }

    fun getWeather(view: android.view.View) {
        val cityName = cityEditText.text.toString()
        val url = "https://danepubliczne.imgw.pl/api/data/synop"

        val client = OkHttpClient()
        val request = Request.Builder()
            .url(url)
            .build()

        GlobalScope.launch(Dispatchers.IO) {
            val response = client.newCall(request).execute()
            val responseData = response.body?.string()

            launch(Dispatchers.Main) {
                if (response.isSuccessful && !responseData.isNullOrEmpty()) {
                    val weatherData = parseWeatherData(responseData, cityName)
                    weatherTextView.text = weatherData
                } else {
                    weatherTextView.text = "Failed to get weather data."
                }
            }
        }
    }

    private fun parseWeatherData(responseData: String, cityName: String): String {
        val jsonArray = JSONArray(responseData)
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            if (jsonObject.getString("stacja") == cityName) {
                val temperature = jsonObject.getString("temperatura").toDouble()
                val wind = jsonObject.getString("predkosc_wiatru").toDouble()
                return "City: $cityName\nTemperature: $temperature°C\nWind Speed: $wind m/s"
            }
        }
        return "Weather data for $cityName not found."
    }
}
