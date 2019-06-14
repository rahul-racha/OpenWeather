package com.rahul.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse

interface WeatherRepository {
    suspend fun fetchLocationWeather(cityName: String, countryCode: String): LiveData<LocationWeatherResponse>
    suspend fun fetchLocationWeatherByID(cityID: String): LiveData<LocationWeatherResponse>

}