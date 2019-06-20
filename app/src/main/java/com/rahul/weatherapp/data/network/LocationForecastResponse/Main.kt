package com.rahul.weatherapp.data.network.LocationForecastResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Main(
    @SerializedName("grnd_level")
    val grndLevel: Double?,
    val humidity: Int?,
    val pressure: Double?,
    @SerializedName("sea_level")
    val seaLevel: Double?,
    val temp: Double?,
    @SerializedName("temp_kf")
    val tempKf: Double?,
    @SerializedName("temp_max")
    val tempMax: Double?,
    @SerializedName("temp_min")
    val tempMin: Double?
): Parcelable