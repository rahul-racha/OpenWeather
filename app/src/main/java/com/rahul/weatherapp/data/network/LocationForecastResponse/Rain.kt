package com.rahul.weatherapp.data.network.LocationForecastResponse


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Rain(
    @SerializedName("3h")
    val h: Double?
): Parcelable