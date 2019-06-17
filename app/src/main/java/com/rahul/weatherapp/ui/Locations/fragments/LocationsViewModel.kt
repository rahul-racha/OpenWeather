package com.rahul.weatherapp.ui.Locations.fragments

import android.app.Application
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.*
import com.google.android.libraries.places.api.model.AddressComponents
import com.google.android.libraries.places.api.model.Place
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

    enum class AddressType(val constant: String) {
        locality("locality"),
        state("administrative_area_level_1"),
        country("country"),
        postalCode("postal_code")
    }

    data class ViewData(var location: Location, var weather: LocationWeatherResponse)
    data class TransientNewPlaceData(val id: String, val postalCode: String?, val stateCode: String, val countryCode: String,
                                     val country: String, val locality: String)

    private lateinit var transientNewPlaceData: TransientNewPlaceData
    private lateinit var savedLocations: List<Location>
    private val weatherRepository: WeatherRepositoryImpl
    private var listViewData = mutableListOf<ViewData>()
    private var _viewStateLiveData: MutableLiveData<ViewState> = MutableLiveData()
    private fun currentViewState(): ViewState = _viewStateLiveData.value!!

    data class ViewState(
        val isLoading: Boolean = true,
        val populateRecyclerViewData: Boolean = false,
        val newViewDataPosition: Int = -1
    )

    val viewStateLiveData: LiveData<ViewState>
        get() = _viewStateLiveData

    fun getListViewData(): List<ViewData> = listViewData
    fun removeItemFromViewData(position: Int) {
        listViewData.removeAt(position)
    }

    init {
        val locationDao = WeatherDatabase(application).locationDao()
        val apiService = OpenWeatherAPIService(ConnectivityInterceptorImpl(application))
        val weatherNetworkDataSourceImpl = WeatherNetworkDataSourceImpl(apiService)
        weatherRepository = WeatherRepositoryImpl(locationDao, weatherNetworkDataSourceImpl)
        handleDefaultPreferences(application)
        _viewStateLiveData.postValue(ViewState())
    }

    private fun handleDefaultPreferences(application: Application) {
        PreferenceManager.getDefaultSharedPreferences(application.applicationContext).apply {
            if (!getBoolean(FIRST_LAUNCH_COMPLETED, false)) {
                initLocationDatabase()
                edit().apply {
                    putBoolean(FIRST_LAUNCH_COMPLETED, true)
                    apply()
                }
            } else {
                loadSavedLocations()
            }
        }
    }

    fun initLocationDatabase() {
        val locationSeed = Location("ChIJ7cmZVwkRsYkRxTxC4m0-2L8", "5387428", "Richmond","VA","US",
            "United States","")
        GlobalScope.launch(Dispatchers.IO) {
            weatherRepository.insert(locationSeed)
            loadSavedLocations()
        }
    }

    fun loadSavedLocations() {
        GlobalScope.launch(Dispatchers.IO) {
            savedLocations = weatherRepository.allSavedLocations
            Log.e("SAVED_LOCATIONS",savedLocations.toString())
            if (savedLocations.size > 0) {
                loadWeatherForSavedLocations(savedLocations)
            }
        }
    }

    fun addNewPlace(place: Place) {
        if (place.addressComponents is AddressComponents) {
            GlobalScope.launch(Dispatchers.IO) {
               if(!(weatherRepository.isPlaceExists(placeID = place.id.toString()))) {
                   _viewStateLiveData.postValue(currentViewState().copy(isLoading = true))
                   var postalCode: String? = null
                   var countryCode: String = ""
                   var country: String = ""
                   var locality: String = ""
                   var stateCode: String = ""
                   var addressComponentList = place.addressComponents!!.asList()
                   for (component in addressComponentList) {
                       if (component.types.contains(AddressType.locality.constant)) {
                           locality = component.name
                       } else if (component.types.contains(AddressType.state.constant)) {
                           stateCode = component.shortName ?: ""
                       } else if (component.types.contains(AddressType.country.constant)) {
                           // MARK: component.shortName for type: country will always have String value
                           countryCode = component.shortName ?: "us"
                           country = component.name
                       } else if (component.types.contains(AddressType.postalCode.constant)) {
                           postalCode = component.name
                       }
                   }
                   transientNewPlaceData = TransientNewPlaceData(place.id.toString(), postalCode, stateCode,
                       countryCode, country, locality)
                   Log.e("TRANSIENT_PLACE", transientNewPlaceData.toString())
                   loadWeatherForTransientPlace(transientNewPlaceData)
               }
            }
//            TODO("show place exists alert")
            Log.e("PLACE_EXISTS", "${place.id.toString()} exists")
        }
//        TODO("show address is empty")
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
            weatherRepository.fetchLocationWeatherInBulk(bulkCityIDStr) { bulkLocationWeatherResponse ->
                createListViewData(locationList, bulkLocationWeatherResponse)
            }
        }
    }

    private fun loadWeatherForTransientPlace(place: TransientNewPlaceData) {
        GlobalScope.launch(Dispatchers.IO) {
            if (place.postalCode != null) {
                weatherRepository.fetchLocationWeatherByZip(place.postalCode!!, place.countryCode) {
                        locationWeatherResponse ->
                    saveLocationToDb(locationWeatherResponse, place)
                }
            } else {
                weatherRepository.fetchLocationWeatherByCity(place.locality, place.countryCode) {
                        locationWeatherResponse ->
                    saveLocationToDb(locationWeatherResponse, place)
                }
            }
        }
    }

    private fun saveLocationToDb(locationWeatherResponse: LocationWeatherResponse, place: TransientNewPlaceData) {
        GlobalScope.launch(Dispatchers.IO) {
            val location = Location(place.id.toString(), locationWeatherResponse.id.toString(), place.locality,
                place.stateCode, place.countryCode, place.country, place.postalCode ?: "")
            weatherRepository.insert(location)
            listViewData.add(ViewData(location, locationWeatherResponse))
            _viewStateLiveData.postValue(currentViewState().copy(isLoading = false,
                newViewDataPosition = listViewData.size-1))
        }
    }

    private fun createListViewData(savedLocationList: List<Location>, bulkResponse: BulkLocationWeatherResponse) {
             if (savedLocationList.size == bulkResponse.cnt) {
                 var i = 0
                 savedLocationList.forEach { location ->
                    listViewData.add(ViewData(location, bulkResponse.list!![i++]!!))
                }
                 _viewStateLiveData.postValue(currentViewState().copy(isLoading = false,
                     populateRecyclerViewData = true))
             }
        Log.e("LIST_VIEW_DATA", listViewData.toString())
    }
}

//                   _viewStateLiveData.postValue(ViewState(isLoading = true, populateRecyclerViewData = false,
//                       newViewData = false))