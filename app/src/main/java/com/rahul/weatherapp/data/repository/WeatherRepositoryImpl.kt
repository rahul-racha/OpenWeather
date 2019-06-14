package com.rahul.weatherapp.data.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rahul.weatherapp.data.database.LocationDao
import com.rahul.weatherapp.data.database.entity.Location
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse
import com.rahul.weatherapp.data.network.WeatherNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeatherRepositoryImpl(
    private val locationDao: LocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource
) : WeatherRepository {

    val allLocations: LiveData<List<Location>> = locationDao.getAllLocations()
    private val currentLocationWeather: MutableLiveData<LocationWeatherResponse> = MutableLiveData<LocationWeatherResponse>()

    init {
        weatherNetworkDataSource.downloadedLocationWeather.observeForever {
            currentLocationWeather.postValue(it)
        }
    }

    override suspend fun fetchLocationWeather(
        cityName: String,
        countryCode: String
    ): LiveData<LocationWeatherResponse> {
        weatherNetworkDataSource.fetchLocationWeather(cityName, countryCode)
        return currentLocationWeather

    }

    override suspend fun fetchLocationWeatherByID(cityID: String): LiveData<LocationWeatherResponse> {
        weatherNetworkDataSource.fetchLocationWeather(cityID)
        return currentLocationWeather
    }

    @WorkerThread
    fun insert(location: Location) {
        GlobalScope.launch(Dispatchers.IO) {
            locationDao.insert(location)
        }
    }

    @WorkerThread
    fun delete(location: Location) {
        GlobalScope.launch(Dispatchers.IO) {
            locationDao.delete(location)
        }
    }
}