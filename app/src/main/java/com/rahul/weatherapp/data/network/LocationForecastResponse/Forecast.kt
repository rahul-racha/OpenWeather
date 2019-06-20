package com.rahul.weatherapp.data.network.LocationForecastResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Forecast(
    val clouds: Clouds?,
    val dt: Int?,
    @SerializedName("dt_txt")
    val dtTxt: String?,
    val main: Main?,
    val rain: Rain?,
    val sys: Sys?,
    val weather: List<Weather?>?,
    val wind: Wind?
): Parcelable