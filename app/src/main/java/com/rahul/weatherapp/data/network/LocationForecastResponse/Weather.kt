package com.rahul.weatherapp.data.network.LocationForecastResponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Weather(
    val description: String?,
    val icon: String?,
    val id: Int?,
    val main: String?
): Parcelable