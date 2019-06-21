package com.rahul.weatherapp.data.network

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.rahul.weatherapp.data.database.entity.Location
import com.rahul.weatherapp.data.network.LocationForecastResponse.LocationForecastResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.BulkLocationWeatherResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse
import com.rahul.weatherapp.internal.MessageBox
import com.rahul.weatherapp.internal.NoConnectivityException

// TODO: Show user exception messages
class WeatherNetworkDataSourceImpl(
    private val openWeatherAPIService: OpenWeatherAPIService
) : WeatherNetworkDataSource {

    private val _locationWeather = MutableLiveData<LocationWeatherResponse>()
    private val _bulkLocationWeather = MutableLiveData<BulkLocationWeatherResponse>()

    override val downloadedLocationWeather: LiveData<LocationWeatherResponse>
        get() = _locationWeather

    override val downloadedBulkLocationWeather: LiveData<BulkLocationWeatherResponse>
        get() = _bulkLocationWeather

    override suspend fun fetchLocationWeatherByZip(zipCode: String, countryCode: String,
                                              callback: ((response: LocationWeatherResponse?,
                                                          messageBox: MessageBox?) -> Unit)) {
        try {
            val fetchedLocationWeather = openWeatherAPIService
                .getLocationWeatherByZip("$zipCode,$countryCode")
                .await()
            callback(fetchedLocationWeather, null)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
            callback(null, MessageBox(title = "Connectivity Error", message = "No internet connection"))
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.localizedMessage)
            callback(null, MessageBox(title = "Error", message = e.localizedMessage))
        }
    }

    override suspend fun fetchLocationWeatherByCity(
        city: String,
        countryCode: String,
        callback: (response: LocationWeatherResponse?, messageBox: MessageBox?) -> Unit
    ) {
        try {
            val fetchedLocationWeather = openWeatherAPIService
                .getLocationWeatherByCity("$city,$countryCode")
                .await()
            callback(fetchedLocationWeather, null)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
            callback(null, MessageBox(title = "Connectivity Error", message = "No internet connection"))
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.localizedMessage)
            callback(null, MessageBox(title = "Error", message = e.localizedMessage))
        }
    }

    override suspend fun fetchLocationWeatherInBulk(cityIDs: String,
                                              callback: ((response: BulkLocationWeatherResponse?,
                                                          messageBox: MessageBox?) -> Unit)) {
        try {
            val fetchedBulkLocationWeather = openWeatherAPIService
                .getLocationWeatherInBulk(cityIDs)
                .await()
            Log.e("BULK_WEATHER", fetchedBulkLocationWeather.toString())
            callback(fetchedBulkLocationWeather, null)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
            callback(null, MessageBox(title = "Connectivity Error", message = "No internet connection"))
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.localizedMessage)
            callback(null, MessageBox(title = "Error", message = e.localizedMessage))
        }
    }

    override suspend fun fetchLocationForecast(cityID: String, callback: (response: LocationForecastResponse?,
                                                                          messageBox: MessageBox?) -> Unit) {
        try {
            val fetchedLocationForecast = openWeatherAPIService
                .getForecast(cityID)
                .await()
            Log.e("LOCATION_FORECAST", fetchedLocationForecast.toString())
            callback(fetchedLocationForecast, null)
        } catch (e: NoConnectivityException) {
            Log.e("Connectivity", "No internet connection", e)
            callback(null, MessageBox(title = "Connectivity Error", message = "No internet connection"))
        } catch (e: Exception) {
            Log.e("EXCEPTION", e.localizedMessage)
            callback(null, MessageBox(title = "Error", message = e.localizedMessage))
        }
    }

}