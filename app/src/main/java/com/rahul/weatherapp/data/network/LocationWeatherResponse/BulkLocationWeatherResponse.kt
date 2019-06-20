package com.rahul.weatherapp.data.network.LocationWeatherResponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BulkLocationWeatherResponse(
    val cnt: Int?,
    val list: List<LocationWeatherResponse?>?
): Parcelable