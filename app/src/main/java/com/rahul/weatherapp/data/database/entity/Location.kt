package com.rahul.weatherapp.data.database.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

object TableProperties {
    const val tableName: String = "location"
    const val placeID: String = "place_id"
    const val cityID: String = "city_id"
    const val cityName: String = "city_name"
    const val state: String = "administrative_area_level_1"
    const val countryCode: String = "country_code"
    const val countryName: String = "country_name"
    const val zipCode: String = "zip_code"
}

@Entity(tableName = TableProperties.tableName) //primaryKeys = arrayOf("city_name", "country_code", "zip_code")
data class Location (
    @ColumnInfo(name = "place_id") var placeID: String,
    @ColumnInfo(name = "city_id") var cityID: String,
    @ColumnInfo(name = "city_name") var cityName: String,
    @ColumnInfo(name = "administrative_area_level_1") var administrativeAreaLevel1: String,
    @ColumnInfo(name = "country_code") var countryCode: String,
    @ColumnInfo(name = "country_name") var countryName: String,
    @ColumnInfo(name = "zip_code") var zipCode: String
) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
}