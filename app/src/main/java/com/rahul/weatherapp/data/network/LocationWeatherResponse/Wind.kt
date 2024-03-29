package com.rahul.weatherapp.data.network.LocationWeatherResponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wind(
    val speed: Double?
): Parcelable