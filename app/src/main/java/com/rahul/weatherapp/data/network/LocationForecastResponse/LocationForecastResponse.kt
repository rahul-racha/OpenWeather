package com.rahul.weatherapp.data.network.LocationForecastResponse

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationForecastResponse(
    val city: City?,
    val cnt: Int?,
    val cod: String?,
    val list: List<Forecast?>?,
    val message: Double?
): Parcelable