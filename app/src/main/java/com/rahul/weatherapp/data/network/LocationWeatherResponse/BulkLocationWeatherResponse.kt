package com.rahul.weatherapp.data.network.LocationWeatherResponse

data class BulkLocationWeatherResponse(
    val cnt: Int?,
    val list: List<LocationWeatherResponse?>?
)