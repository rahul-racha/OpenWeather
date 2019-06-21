package com.rahul.weatherapp.data.repository

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rahul.weatherapp.data.database.LocationDao
import com.rahul.weatherapp.data.database.entity.Location
import com.rahul.weatherapp.data.network.LocationForecastResponse.LocationForecastResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.BulkLocationWeatherResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse
import com.rahul.weatherapp.data.network.WeatherNetworkDataSource
import com.rahul.weatherapp.internal.MessageBox
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
        callback: ((response: LocationWeatherResponse?, messageBox: MessageBox?) -> Unit)
    ) {
        weatherNetworkDataSource.fetchLocationWeatherByZip(zipCode, countryCode, callback)
    }

    override suspend fun fetchLocationWeatherByCity(
        city: String,
        countryCode: String,
        callback: (response: LocationWeatherResponse?, messageBox: MessageBox?) -> Unit
    ) {
        weatherNetworkDataSource.fetchLocationWeatherByCity(city,countryCode, callback)
    }

    override suspend fun fetchLocationWeatherInBulk(cityIDs: String,
                                              callback: ((response: BulkLocationWeatherResponse?,
                                                          messageBox: MessageBox?) -> Unit)) {
        weatherNetworkDataSource.fetchLocationWeatherInBulk(cityIDs,callback)
    }

    override suspend fun fetchLocationForecast(cityID: String, callback: (response: LocationForecastResponse?,
                                                                          messageBox: MessageBox?) -> Unit) {
        weatherNetworkDataSource.fetchLocationForecast(cityID, callback)
    }

    @WorkerThread
    override fun insert(vararg locations: Location) {
        for (loc in locations) {
            locationDao.insert(loc)
        }
    }

    @WorkerThread
    override fun delete(vararg placeIDS: String) {
        for (id in placeIDS) {
            locationDao.delete(id)
        }
    }

    @WorkerThread
    override fun isPlaceExists(placeID: String): Boolean {
        return locationDao.isPlaceExists(placeID)
    }

    @WorkerThread
    override fun update(existingPlaceIDS: Array<String>, vararg locations: Location): Boolean {
        if (locations.size != existingPlaceIDS.size) {
            return false
        }
        var index = 0
        for (loc in locations) {
            locationDao.update(existingPlaceIDS[index], placeID = loc.placeID, cityID = loc.cityID, cityName = loc.cityName,
                state = loc.administrativeAreaLevel1, countryCode = loc.countryCode, countryName = loc.countryName,
                zipCode = loc.zipCode)
            index += 1
        }
        return true
    }
}