package com.rahul.weatherapp.data.network.LocationWeatherResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Main(
    val humidity: Int?,
    val pressure: Double?,
    val temp: Double?,
    @SerializedName("temp_max")
    val tempMax: Double?,
    @SerializedName("temp_min")
    val tempMin: Double?
): Parcelable