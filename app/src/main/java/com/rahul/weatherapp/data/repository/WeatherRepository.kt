package com.rahul.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.rahul.weatherapp.data.network.LocationWeatherResponse.BulkLocationWeatherResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse

interface WeatherRepository {
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
}