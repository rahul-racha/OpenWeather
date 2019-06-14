package com.rahul.weatherapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rahul.weatherapp.data.database.entity.Location
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse
import com.rahul.weatherapp.internal.NoConnectivityException

class WeatherNetworkDataSourceImpl(
    private val openWeatherAPIService: OpenWeatherAPIService
) : WeatherNetworkDataSource {

    private val _locationWeather = MutableLiveData<LocationWeatherResponse>()
    override val downloadedLocationWeather: LiveData<LocationWeatherResponse>
        get() = _locationWeather

    override suspend fun fetchLocationWeather(cityName: String, countryCode: String) {
        try {
            val fetchedLocationWeather = openWeatherAPIService
                .getLocationWeather("$cityName,$countryCode")
                .await()
            _locationWeather.postValue(fetchedLocationWeather)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }

    override suspend fun fetchLocationWeather(cityID: String) {
        try {
            val fetchedLocationWeather = openWeatherAPIService
                .getLocationWeatherByID(cityID)
                .await()
            _locationWeather.postValue(fetchedLocationWeather)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
        }
    }

}