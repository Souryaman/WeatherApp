package com.example.weatherapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.SearchView
import android.widget.TextView
import com.example.weatherapp.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// 82d9a542d7bd5302f24f5bdfa5127a3f
class MainActivity : AppCompatActivity() {

    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        fetchWeatherData("New Delhi")
        SearchCity()
    }

    private fun SearchCity() {
        val searchView = findViewById<SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                if (p0 != null) {
                    fetchWeatherData(p0)
                }
                return true
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return true
            }

        })
    }

    private fun fetchWeatherData(cityName:String) {
        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)

        val response = retrofit.getWeatherData(cityName,"59476b2fda65c009f85504b6d3615e55","metric")
        response.enqueue(object : Callback<WeatherApp>{
            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {
                val responseBody = response.body()
                if(response.isSuccessful && responseBody != null){
                    val temperature = responseBody.main.temp.toString()
                    val humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.pressure
                    val condition = responseBody.weather.firstOrNull()?.main?:"unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min


                    val temp = findViewById<TextView>(R.id.temp)
                    temp.text = "$temperature ℃"

                    val hum = findViewById<TextView>(R.id.humidity)
                    hum.text = "$humidity %"

                    val wS = findViewById<TextView>(R.id.wind)
                    wS.text = "$windSpeed m/s"

                    val sR = findViewById<TextView>(R.id.sunrise)
                    sR.text = "${time(sunRise)}"

                    val sS = findViewById<TextView>(R.id.sunset)
                    sS.text = "${time(sunSet)}"

                    val sL = findViewById<TextView>(R.id.sea)
                    sL.text = "$seaLevel hPa"

                    val cond = findViewById<TextView>(R.id.conditions)
                    cond.text = "$condition"

                    val mT = findViewById<TextView>(R.id.maxtemp)
                    mT.text = "Max : $maxTemp ℃"

                    val minT = findViewById<TextView>(R.id.mintemp)
                    minT.text = "Min : $minTemp ℃"

                    val day = findViewById<TextView>(R.id.day)
                    day.text = dayName(System.currentTimeMillis())

                    val date = findViewById<TextView>(R.id.date)
                    date.text = date()

                    val cN = findViewById<TextView>(R.id.cityName)
                    cN.text = "$cityName"

                    val weat = findViewById<TextView>(R.id.weath)
                    weat.text = "$condition"


                    changeImageAccordingToWeather(condition )


                }
            }

            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })


    }

    private fun changeImageAccordingToWeather(conditions: String) {
        Log.d("cond", "Weather conditions: $conditions") // Debug log


        when (conditions) {
            "clear sky", "sunny", "clear" -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }

            "light rain", "drizzle", "moderate rain", "showers", "heavy rain" -> {
                binding.root.setBackgroundResource(R.drawable.rain_background)
                binding.lottieAnimationView.setAnimation(R.raw.rain)
            }

            "partly clouds", "clouds", "overcast", "mist", "foggy", "haze" -> {
                binding.root.setBackgroundResource(R.drawable.cloud_background)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
            }

            "light snow", "moderate snow", "heavy snow", "blizzard" -> {
                binding.root.setBackgroundResource(R.drawable.snow_background)
                binding.lottieAnimationView.setAnimation(R.raw.snow)
            }

            else -> {
                binding.root.setBackgroundResource(R.drawable.sunny_background)
                binding.lottieAnimationView.setAnimation(R.raw.sun)
            }
        }

        binding.lottieAnimationView.playAnimation()
    }



    fun dayName(timestamp: Long) : String{
        val sdf = SimpleDateFormat("EEEE", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun date() : String{
        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
        return sdf.format((Date()))
    }

    fun time(timestamp: Long) : String{
        val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }
}