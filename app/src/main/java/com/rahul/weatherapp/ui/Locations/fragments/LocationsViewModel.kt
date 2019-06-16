package com.rahul.weatherapp.ui.Locations.fragments

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.rahul.weatherapp.data.database.entity.Location
import com.rahul.weatherapp.data.database.WeatherDatabase
import com.rahul.weatherapp.data.network.ConnectivityInterceptorImpl
import com.rahul.weatherapp.data.network.LocationWeatherResponse.BulkLocationWeatherResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse
import com.rahul.weatherapp.data.network.OpenWeatherAPIService
import com.rahul.weatherapp.data.network.WeatherNetworkDataSourceImpl
import com.rahul.weatherapp.data.repository.WeatherRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        val FIRST_LAUNCH_COMPLETED: String = "first_launch_completed"
        val ADD_PLACE_ACTIVITY_CODE: Int = 1
    }

    data class ViewData(val location: Location, val weather: LocationWeatherResponse)

    private val weatherRepository: WeatherRepositoryImpl
    private lateinit var allLocationsLiveData: LiveData<List<Location>>
    private lateinit var bulkLocationWeatherLiveData: LiveData<BulkLocationWeatherResponse>
    private var listViewData = mutableListOf<ViewData>()
    private lateinit var savedLocations: List<Location>

    init {
        val locationDao = WeatherDatabase(application).locationDao()
        val apiService = OpenWeatherAPIService(ConnectivityInterceptorImpl(application))
        val weatherNetworkDataSourceImpl = WeatherNetworkDataSourceImpl(apiService)
        weatherRepository = WeatherRepositoryImpl(locationDao, weatherNetworkDataSourceImpl)
    }

    fun initLocationDatabase() {
        val locationSeed = Location("524901", "Richmond", "us", "United States",
            "23173")
        GlobalScope.launch(Dispatchers.IO) {
            weatherRepository.insert(locationSeed)
        }
    }

    fun loadSavedLocations() {
        GlobalScope.launch(Dispatchers.IO) {
            savedLocations = weatherRepository.allSavedLocations
            Log.e("SAVED_LOCATIONS",savedLocations.toString())
            loadWeatherForSavedLocations(savedLocations)
        }
    }

    private fun loadWeatherForSavedLocations(locationList: List<Location>) {
        var bulkCityIDStr = ""
        locationList.forEach { location ->
            bulkCityIDStr += location.cityID
            bulkCityIDStr += ","
        }
        bulkCityIDStr = bulkCityIDStr.removeSuffix(",")
        Log.e("BULK_CITIES_ID", bulkCityIDStr)
        GlobalScope.launch(Dispatchers.IO) {
            weatherRepository.fetchLocationWeather(bulkCityIDStr) { bulkLocationWeatherResponse ->
                createListViewData(savedLocations, bulkLocationWeatherResponse)
            }
        }
    }

    private fun createListViewData(savedLocationList: List<Location>, bulkResponse: BulkLocationWeatherResponse) {
             if (savedLocationList.size == bulkResponse.cnt) {
                 var i = 0
                 savedLocationList.forEach { location ->
                    listViewData.add(ViewData(location, bulkResponse.list!![i++]!!))
                }
             }
        Log.e("LIST_VIEW_DATA", listViewData.toString())
    }
}
