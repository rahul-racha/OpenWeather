package com.rahul.weatherapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rahul.weatherapp.data.database.entity.Location
import com.rahul.weatherapp.data.network.LocationWeatherResponse.BulkLocationWeatherResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse
import com.rahul.weatherapp.internal.NoConnectivityException

class WeatherNetworkDataSourceImpl(
    private val openWeatherAPIService: OpenWeatherAPIService
) : WeatherNetworkDataSource {

    private val _locationWeather = MutableLiveData<LocationWeatherResponse>()
    private val _bulkLocationWeather = MutableLiveData<BulkLocationWeatherResponse>()

    override val downloadedLocationWeather: LiveData<LocationWeatherResponse>
        get() = _locationWeather

    override val downloadedBulkLocationWeather: LiveData<BulkLocationWeatherResponse>
        get() = _bulkLocationWeather

    override suspend fun fetchLocationWeather(zipCode: String, countryCode: String,
                                              callback: ((response: LocationWeatherResponse) -> Unit)) {
        try {
            val fetchedLocationWeather = openWeatherAPIService
                .getLocationWeather("$zipCode,$countryCode")
                .await()
//            _locationWeather.postValue(fetchedLocationWeather)
            callback(fetchedLocationWeather)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }

    override suspend fun fetchLocationWeather(cityID: String,
                                              callback: ((response: BulkLocationWeatherResponse) -> Unit)) {
        try {
            val fetchedBulkLocationWeather = openWeatherAPIService
                .getLocationWeatherByID(cityID)
                .await()
//            _bulkLocationWeather.postValue(fetchedBulkLocationWeather)
            callback(fetchedBulkLocationWeather)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }

}