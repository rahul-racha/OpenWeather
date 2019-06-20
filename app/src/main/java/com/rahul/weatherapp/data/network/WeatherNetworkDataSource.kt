package com.rahul.weatherapp.data.network

import androidx.lifecycle.LiveData
import com.rahul.weatherapp.data.network.LocationForecastResponse.LocationForecastResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.BulkLocationWeatherResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse

interface WeatherNetworkDataSource {

    val downloadedLocationWeather:  LiveData<LocationWeatherResponse>
    val downloadedBulkLocationWeather: LiveData<BulkLocationWeatherResponse>

    suspend fun fetchLocationWeatherByZip(
        zipCode: String,
        countryCode: String,
        callback: ((response: LocationWeatherResponse) -> Unit)
    )

    suspend fun fetchLocationWeatherByCity(
        city: String,
        countryCode: String,
        callback: ((response: LocationWeatherResponse) -> Unit)
    )

    suspend fun fetchLocationWeatherInBulk(
        cityIDs: String,
        callback: ((response: BulkLocationWeatherResponse) -> Unit)
    )

    suspend fun fetchLocationForecast(
        cityID: String,
        callback: ((response: LocationForecastResponse) -> Unit)
    )
}