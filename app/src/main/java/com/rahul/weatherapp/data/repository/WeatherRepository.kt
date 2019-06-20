package com.rahul.weatherapp.data.repository

import androidx.lifecycle.LiveData
import com.rahul.weatherapp.data.database.entity.Location
import com.rahul.weatherapp.data.network.LocationForecastResponse.LocationForecastResponse
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

    suspend fun fetchLocationForecast(
        cityID: String,
        callback: ((response: LocationForecastResponse) -> Unit)
    )

    fun insert(vararg locations: Location)
    fun delete(vararg placeIDS: String)
    fun isPlaceExists(placeID: String): Boolean
    fun update(existingPlaceIDS: Array<String>, vararg locations: Location): Boolean
}