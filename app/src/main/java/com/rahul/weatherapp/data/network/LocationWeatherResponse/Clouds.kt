package com.rahul.weatherapp.data.network.LocationWeatherResponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Clouds(
    val all: Int?
): Parcelable