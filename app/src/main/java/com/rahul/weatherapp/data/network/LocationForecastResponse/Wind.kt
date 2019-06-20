package com.rahul.weatherapp.data.network.LocationForecastResponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Wind(
    val deg: Double?,
    val speed: Double?
): Parcelable