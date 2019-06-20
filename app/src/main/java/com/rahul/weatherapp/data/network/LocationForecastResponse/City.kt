package com.rahul.weatherapp.data.network.LocationForecastResponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class City(
    val coord: Coord?,
    val country: String?,
    val id: Int?,
    val name: String?,
    val timezone: Int?
): Parcelable