package com.rahul.weatherapp.data.network

import androidx.lifecycle.LiveData
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse

interface WeatherNetworkDataSource {

    val downloadedLocationWeather:  LiveData<LocationWeatherResponse>

    suspend fun fetchLocationWeather(
        cityName: String,
        countryCode: String
    )

    suspend fun fetchLocationWeather(
        cityID: String
    )
}