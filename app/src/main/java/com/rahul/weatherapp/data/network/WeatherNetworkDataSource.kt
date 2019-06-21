package com.rahul.weatherapp.data.network

import androidx.lifecycle.LiveData
import com.rahul.weatherapp.data.network.LocationForecastResponse.LocationForecastResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.BulkLocationWeatherResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse
import com.rahul.weatherapp.internal.MessageBox

interface WeatherNetworkDataSource {

    val downloadedLocationWeather:  LiveData<LocationWeatherResponse>
    val downloadedBulkLocationWeather: LiveData<BulkLocationWeatherResponse>

    suspend fun fetchLocationWeatherByZip(
        zipCode: String,
        countryCode: String,
        callback: ((response: LocationWeatherResponse?, messageBox: MessageBox?) -> Unit)
    )

    suspend fun fetchLocationWeatherByCity(
        city: String,
        countryCode: String,
        callback: ((response: LocationWeatherResponse?, messageBox: MessageBox?) -> Unit)
    )

    suspend fun fetchLocationWeatherInBulk(
        cityIDs: String,
        callback: ((response: BulkLocationWeatherResponse?, messageBox: MessageBox?) -> Unit)
    )

    suspend fun fetchLocationForecast(
        cityID: String,
        callback: ((response: LocationForecastResponse?, messageBox: MessageBox?) -> Unit)
    )
}