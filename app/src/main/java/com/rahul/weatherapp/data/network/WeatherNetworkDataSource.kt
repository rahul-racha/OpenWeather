package com.rahul.weatherapp.data.network

import androidx.lifecycle.LiveData
import com.rahul.weatherapp.data.network.LocationWeatherResponse.BulkLocationWeatherResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse

interface WeatherNetworkDataSource {

    val downloadedLocationWeather:  LiveData<LocationWeatherResponse>
    val downloadedBulkLocationWeather: LiveData<BulkLocationWeatherResponse>

    suspend fun fetchLocationWeather(
        zipCode: String,
        countryCode: String,
        callback: ((response: LocationWeatherResponse) -> Unit)
    )

    suspend fun fetchLocationWeather(
        cityID: String,
        callback: ((response: BulkLocationWeatherResponse) -> Unit)
    )
}