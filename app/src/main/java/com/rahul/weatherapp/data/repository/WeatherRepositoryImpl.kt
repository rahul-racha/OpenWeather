package com.rahul.weatherapp.data.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rahul.weatherapp.data.database.LocationDao
import com.rahul.weatherapp.data.database.entity.Location
import com.rahul.weatherapp.data.network.LocationWeatherResponse.BulkLocationWeatherResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse
import com.rahul.weatherapp.data.network.WeatherNetworkDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class WeatherRepositoryImpl(
    private val locationDao: LocationDao,
    private val weatherNetworkDataSource: WeatherNetworkDataSource
) : WeatherRepository {

    // Mark: Call on worker thread
    val allSavedLocations: List<Location>
        get() = locationDao.getAllLocations()
    private val currentLocationWeather = MutableLiveData<LocationWeatherResponse>()
    private val bulkLocationWeather = MutableLiveData<BulkLocationWeatherResponse>()

    init {
        weatherNetworkDataSource.downloadedLocationWeather.observeForever {
            currentLocationWeather.postValue(it)
        }
        weatherNetworkDataSource.downloadedBulkLocationWeather.observeForever {
            bulkLocationWeather.postValue(it)
        }
    }

    override suspend fun fetchLocationWeatherByZip(
        zipCode: String,
        countryCode: String,
        callback: ((response: LocationWeatherResponse) -> Unit)
    ) {
        weatherNetworkDataSource.fetchLocationWeatherByZip(zipCode, countryCode, callback)
    }

    override suspend fun fetchLocationWeatherByCity(
        city: String,
        countryCode: String,
        callback: (response: LocationWeatherResponse) -> Unit
    ) {
        weatherNetworkDataSource.fetchLocationWeatherByCity(city,countryCode, callback)
    }

    override suspend fun fetchLocationWeatherInBulk(cityIDs: String,
                                              callback: ((response: BulkLocationWeatherResponse) -> Unit)) {
        weatherNetworkDataSource.fetchLocationWeatherInBulk(cityIDs,callback)
    }

    @WorkerThread
    fun insert(vararg locations: Location) {
        for (loc in locations) {
            locationDao.insert(loc)
        }
    }

    @WorkerThread
    fun delete(vararg locations: Location) {
        for (loc in locations) {
            locationDao.delete(loc)
        }
    }

    @WorkerThread
    fun isPlaceExists(placeID: String): Boolean {
        return locationDao.isPlaceExists(placeID)
    }
}