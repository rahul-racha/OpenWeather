package com.rahul.weatherapp.ui.fragments.LocationForecast

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel;
import com.rahul.weatherapp.data.network.LocationForecastResponse.Forecast
import com.rahul.weatherapp.data.network.LocationForecastResponse.LocationForecastResponse
import com.rahul.weatherapp.internal.getDayOfWeek
import com.rahul.weatherapp.ui.fragments.LocationWeather.LocationsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


data class ForecastParentModel(val day: String, val date: String, val childList: MutableList<Forecast>)

class ForecastViewModel : ViewModel() {
    private lateinit var locationForecastResponse: LocationForecastResponse
    private lateinit var locationViewData: LocationsViewModel.ViewData
    private var parentList: MutableList<ForecastParentModel> = mutableListOf()
    private val militaryTimeMap: HashMap<String, String> = hashMapOf("00:00:00" to "12 AM", "03:00:00" to "3 AM",
        "06:00:00" to "6 AM", "09:00:00" to "9 AM", "12:00:00" to "12 PM", "15:00:00" to "3 PM", "18:00:00" to "6 PM",
        "21:00:00" to "9 PM")

    data class ViewState(val isLoading: Boolean = true, val modelData: List<ForecastParentModel>? = null)

    private val _forecastLiveData = MutableLiveData<ViewState>()

    init {
        _forecastLiveData.postValue(ViewState())
    }

    fun forecastLiveData(): LiveData<ViewState> = _forecastLiveData

    fun setActionData(response: LocationForecastResponse, viewData: LocationsViewModel.ViewData) {
        locationForecastResponse = response
        locationViewData = viewData
        GlobalScope.launch(Dispatchers.Default) {
            prepareModel()
            _forecastLiveData.postValue(ViewState(isLoading = false, modelData = parentList))
        }
    }

    fun getParentList() = parentList

    fun prepareModel() {
        val forecastList = locationForecastResponse.list; val size = forecastList!!.size - 1
        val delimiter = "\\s+".toRegex(); var parentIndex = 0; var outerLoop = 0; var innerLoop = 0
        while (outerLoop <= size) {
            var dateText = forecastList[outerLoop]!!.dtTxt; var dateComponents: List<String>
            if (dateText != null) {
                dateComponents = dateText.split(delimiter)
            } else {
                dateComponents = listOf()
            }
            val datePattern: String = dateComponents[0]
            val day: String = getDayOfWeek(dateComponents[0])


            var childForecast: MutableList<Forecast> = mutableListOf()
            var childIndex = 0
            innerLoop = outerLoop
            while (innerLoop <= size) {
                dateText = forecastList[innerLoop]!!.dtTxt
                if (dateText != null) {
                    dateComponents = dateText.split(delimiter)
                    if (dateComponents[0].contains(datePattern, ignoreCase = true)) {
                        val temp = Forecast(clouds = forecastList[innerLoop]?.clouds, dt = forecastList[innerLoop]?.dt,
                            dtTxt = forecastList[innerLoop]?.dtTxt, main = forecastList[innerLoop]?.main,
                            rain = forecastList[innerLoop]?.rain, sys = forecastList[innerLoop]?.sys,
                            weather = forecastList[innerLoop]?.weather, wind = forecastList[innerLoop]?.wind,
                            customTime = militaryTimeMap[dateComponents[1]])
                        childForecast.add(childIndex, temp)
                        childIndex += 1
                        innerLoop += 1
                    } else {
                        outerLoop = innerLoop
                        break
                    }
                } else {
                    innerLoop += 1
                }
            }
            if (innerLoop > size) {
                outerLoop = innerLoop
            }
            // use parsed day, date and time
            parentList.add(parentIndex, ForecastParentModel(day = day, date = datePattern, childList = childForecast))
            parentIndex += 1
        }

        Log.e("BUILT_DATA_FORECAST", parentList.size.toString())
    }

}