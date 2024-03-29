package com.rahul.weatherapp.ui.fragments.LocationWeather

import android.app.Application
import android.os.Parcelable
import android.preference.PreferenceManager
import android.util.Log
import androidx.lifecycle.*
import com.google.android.libraries.places.api.model.AddressComponents
import com.google.android.libraries.places.api.model.Place
import com.rahul.weatherapp.data.database.entity.Location
import com.rahul.weatherapp.data.database.WeatherDatabase
import com.rahul.weatherapp.data.network.ConnectivityInterceptorImpl
import com.rahul.weatherapp.data.network.LocationForecastResponse.LocationForecastResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.BulkLocationWeatherResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse
import com.rahul.weatherapp.data.network.OpenWeatherAPIService
import com.rahul.weatherapp.data.network.WeatherNetworkDataSourceImpl
import com.rahul.weatherapp.data.repository.WeatherRepositoryImpl
import com.rahul.weatherapp.internal.MessageBox
import kotlinx.android.parcel.Parcelize
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class LocationsViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        val FIRST_LAUNCH_COMPLETED: String = "first_launch_completed"
        val ADD_PLACE_ACTIVITY_CODE: Int = 1
        val EDIT_PLACE_ACTIVITY_CODE: Int = 2
    }

    enum class AddressType(val constant: String) {
        locality("locality"),
        state("administrative_area_level_1"),
        country("country"),
        postalCode("postal_code")
    }

    @Parcelize
    data class ViewData(var location: Location, var weather: LocationWeatherResponse): Parcelable
    data class TransientNewPlaceData(val id: String, val postalCode: String?, val stateCode: String, val countryCode: String,
                                     val country: String, val locality: String)
    data class ForecastData(val forcastResponse: LocationForecastResponse, val position: Int)

    private lateinit var transientNewPlaceData: TransientNewPlaceData
    private lateinit var savedLocations: List<Location>
    private val weatherRepository: WeatherRepositoryImpl
    private var listViewData = mutableListOf<ViewData>()
    private var _viewStateLiveData: MutableLiveData<ViewState> = MutableLiveData()
    private fun currentViewState(): ViewState = _viewStateLiveData.value!!
    private var deletedLocationMap: HashMap<String, ViewData> = hashMapOf()

    data class ViewState(
        val isLoading: Boolean = true,
        val populateRecyclerViewData: Boolean = false,
        val newViewDataPosition: Int = -1,
        val updateViewDataAtPosition: Int = -1,
        val forecastData: ForecastData? = null,
        val messageBox: MessageBox? = null
    )

    val viewStateLiveData: LiveData<ViewState>
        get() = _viewStateLiveData

    init {
        val locationDao = WeatherDatabase(application).locationDao()
        val apiService = OpenWeatherAPIService(ConnectivityInterceptorImpl(application))
        val weatherNetworkDataSourceImpl = WeatherNetworkDataSourceImpl(apiService)
        weatherRepository = WeatherRepositoryImpl(locationDao, weatherNetworkDataSourceImpl)
        handleDefaultPreferences(application)
        _viewStateLiveData.postValue(ViewState())
    }

    fun getListViewData(): List<ViewData> = listViewData

    fun removeItemFromViewData(position: Int) {
        listViewData.removeAt(position)
        if (listViewData.isEmpty()) {
            _viewStateLiveData.value =
                ViewState(
                    isLoading = false, populateRecyclerViewData = false,
                    newViewDataPosition = -1, updateViewDataAtPosition = -1, forecastData = null, messageBox = null
                )
        }
    }

    fun addItemToViewData(position: Int, item: ViewData) {
        listViewData.add(position, item)
    }

    fun clearListViewData() {
        listViewData.clear()
    }

    fun clearLocationsFromMap() {
        deletedLocationMap.clear()
    }

    fun addDeletedLocationToMap(viewData: ViewData) {
        deletedLocationMap[viewData.location.placeID] = viewData
        GlobalScope.launch(Dispatchers.IO) {
            weatherRepository.delete(viewData.location.placeID)
        }
    }

    fun restoreLocation(position: Int, viewData: ViewData) {
        listViewData.add(position, viewData)
        deletedLocationMap.clear()
        GlobalScope.launch(Dispatchers.IO) {
            weatherRepository.insert(viewData.location)
            if (!listViewData.isEmpty()) {
                _viewStateLiveData.postValue(
                    ViewState(
                        isLoading = false, populateRecyclerViewData = false,
                        newViewDataPosition = -1, updateViewDataAtPosition = -1, forecastData = null, messageBox = null
                    )
                )
            }
        }
    }

    fun replaceItem(position: Int, place: Place) {
        loadNewPlace(place) { locationWeatherResponse, mBox ->
            if (locationWeatherResponse != null) {
                val existingPlaceID = listViewData[position].location.placeID
                val location = Location(
                    placeID = transientNewPlaceData.id.toString(),
                    cityID = locationWeatherResponse.id.toString(), cityName = transientNewPlaceData.locality,
                    countryCode = transientNewPlaceData.countryCode, countryName = transientNewPlaceData.country,
                    zipCode = transientNewPlaceData.postalCode ?: "",
                    administrativeAreaLevel1 = transientNewPlaceData.stateCode
                )
                GlobalScope.launch(Dispatchers.IO) {
                    if (weatherRepository.update(arrayOf(existingPlaceID), location)) {
                        listViewData[position] =
                            ViewData(
                                location,
                                locationWeatherResponse!!
                            )
                        _viewStateLiveData.postValue(
                            currentViewState().copy(
                                isLoading = false,
                                populateRecyclerViewData = false,
                                newViewDataPosition = -1,
                                updateViewDataAtPosition = position,
                                forecastData = null,
                                messageBox = null
                            )
                        )
                    }
                }
            } else {
                _viewStateLiveData.postValue(
                    ViewState(
                        isLoading = false,
                        populateRecyclerViewData = false,
                        newViewDataPosition = -1,
                        updateViewDataAtPosition = -1,
                        forecastData = null,
                        messageBox = mBox
                    )
                )
            }
        }
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
            } else {
                // Mark: currentViewState() is not yet set.
                _viewStateLiveData.postValue(
                    ViewState(
                        isLoading = false, populateRecyclerViewData = true,
                        newViewDataPosition = -1, updateViewDataAtPosition = -1, forecastData = null, messageBox = null
                    )
                )
            }
        }
    }

    fun loadNewPlace(place: Place, callback: ((response: LocationWeatherResponse?, messageBox: MessageBox?) -> Unit)? = null) {
        if (place.addressComponents is AddressComponents) {
            GlobalScope.launch(Dispatchers.IO) {
               if(!(weatherRepository.isPlaceExists(placeID = place.id.toString()))) {
                   _viewStateLiveData.postValue(currentViewState().copy(isLoading = true,
                       populateRecyclerViewData = false, newViewDataPosition = -1,
                       updateViewDataAtPosition = -1, forecastData = null, messageBox = null))
                   var postalCode: String? = null; var countryCode: String = ""; var country: String = ""
                   var locality: String = ""; var stateCode: String = ""
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
                   transientNewPlaceData =
                       TransientNewPlaceData(
                           place.id.toString(), postalCode, stateCode,
                           countryCode, country, locality
                       )
                   Log.e("TRANSIENT_PLACE", transientNewPlaceData.toString())

                   if (callback == null) {
                       loadWeatherForTransientPlace(transientNewPlaceData) { locationWeatherResponse, mBox ->
                           if (locationWeatherResponse != null) {
                               saveLocationToDb(locationWeatherResponse!!, transientNewPlaceData)
                           } else {
                               _viewStateLiveData.postValue(
                                   ViewState(
                                       isLoading = false,
                                       populateRecyclerViewData = false,
                                       newViewDataPosition = -1,
                                       updateViewDataAtPosition = -1,
                                       forecastData = null,
                                       messageBox = mBox
                                   )
                               )
                           }
                       }
                   } else {
                       loadWeatherForTransientPlace(transientNewPlaceData) { locationWeather, mBox ->
                           callback(locationWeather, mBox)
                       }
                   }
               } else {
                   Log.e("PLACE_EXISTS", "${place.id.toString()} exists")
                   val mBox = MessageBox(title = "Alert", message = "Place already exists")
                   _viewStateLiveData.postValue(
                       ViewState(
                           isLoading = false,
                           populateRecyclerViewData = false,
                           newViewDataPosition = -1,
                           updateViewDataAtPosition = -1,
                           forecastData = null,
                           messageBox = mBox

                       )
                   )
               }
            }
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
            weatherRepository.fetchLocationWeatherInBulk(bulkCityIDStr) { bulkLocationWeatherResponse, mBox ->
                if (bulkLocationWeatherResponse != null) {
                    createListViewData(locationList, bulkLocationWeatherResponse!!)
                } else {
                    _viewStateLiveData.postValue(
                        ViewState(
                            isLoading = false, populateRecyclerViewData = false,
                            newViewDataPosition = -1, updateViewDataAtPosition = -1, messageBox = mBox
                        )
                    )
                }
            }
        }
    }

    private fun loadWeatherForTransientPlace(place: TransientNewPlaceData,
                                             callback: ((response: LocationWeatherResponse?, messageBox: MessageBox?)
                                             -> Unit)) {
        GlobalScope.launch(Dispatchers.IO) {
            if (place.postalCode != null) {
                weatherRepository.fetchLocationWeatherByZip(place.postalCode!!, place.countryCode) { locationWeather,
                                                                                                     mBox ->
                    callback(locationWeather, mBox)
                }
            } else {
                weatherRepository.fetchLocationWeatherByCity(place.locality, place.countryCode) { locationWeather,
                                                                                                  mBox ->
                    callback(locationWeather, mBox)
                }
            }
        }
    }

    private fun saveLocationToDb(locationWeatherResponse: LocationWeatherResponse, place: TransientNewPlaceData) {
        GlobalScope.launch(Dispatchers.IO) {
            val location = Location(place.id.toString(), locationWeatherResponse.id.toString(), place.locality,
                place.stateCode, place.countryCode, place.country, place.postalCode ?: "")
            weatherRepository.insert(location)
            listViewData.add(
                ViewData(
                    location,
                    locationWeatherResponse
                )
            )
            _viewStateLiveData.postValue(currentViewState().copy(isLoading = false,
                newViewDataPosition = listViewData.size-1, updateViewDataAtPosition = -1, forecastData = null,
                messageBox = null))
        }
    }

    private fun createListViewData(savedLocationList: List<Location>, bulkResponse: BulkLocationWeatherResponse) {
        // Mark: Open Weather API not giving all results for group city IDS
        if (bulkResponse.cnt != null && bulkResponse.cnt!! > 0) {
            val savedLocListSize = savedLocationList.size; val savedLocIndexSize = savedLocListSize - 1
            val weatherList = bulkResponse.list!!; val bulkIndexSize = weatherList!!.size - 1
            for (i in 0..savedLocIndexSize) {
                for (j in 0..bulkIndexSize) {
                    if (savedLocationList[i].cityID == weatherList[j]!!.id!!.toString()) {
                        listViewData.add(
                            ViewData(
                                savedLocationList[i],
                                weatherList[j]!!
                            )
                        )
                        break
                    }
                }
            }
        }
        Log.e("LIST_VIEW_DATA", listViewData.toString())
        _viewStateLiveData.postValue(currentViewState().copy(isLoading = false,
            populateRecyclerViewData = true, newViewDataPosition = -1, updateViewDataAtPosition = -1,
            forecastData = null, messageBox = null))
    }

    fun fetchForecastForLocation(position: Int) {
        val cityID = listViewData[position].location.cityID
        _viewStateLiveData.postValue(
            ViewState(
                isLoading = true, populateRecyclerViewData = false,
                newViewDataPosition = -1, updateViewDataAtPosition = -1, forecastData = null, messageBox = null
            )
        )
        GlobalScope.launch(Dispatchers.IO) {
            weatherRepository.fetchLocationForecast(cityID) { forecastWeather, mBox ->
                if (forecastWeather != null) {
                    _viewStateLiveData.postValue(
                        ViewState(
                            isLoading = false,
                            populateRecyclerViewData = false,
                            newViewDataPosition = -1,
                            updateViewDataAtPosition = -1,
                            forecastData = ForecastData(
                                forecastWeather!!,
                                position
                            ),
                            messageBox = null
                        )
                    )
                } else {
                    _viewStateLiveData.postValue(
                        ViewState(
                            isLoading = false,
                            populateRecyclerViewData = false,
                            newViewDataPosition = -1,
                            updateViewDataAtPosition = -1,
                            forecastData = null,
                            messageBox = mBox
                        )
                    )
                }
            }
        }
    }

    fun resetForecastState() {
        _viewStateLiveData.postValue(currentViewState().copy(forecastData = null))
    }
}