package com.rahul.weatherapp.data.network.LocationForecastResponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Sys(
    val pod: String?
): Parcelable