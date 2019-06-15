package com.rahul.weatherapp.data.database.entity

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

object TableProperties {
    const val tableName: String = "location"
    const val filterASC: String = "city_name"
}

@Entity(tableName = TableProperties.tableName) //primaryKeys = arrayOf("city_name", "country_code", "zip_code")
data class Location (
    @PrimaryKey @ColumnInfo(name = "id") var cityID: String,
    @ColumnInfo(name = "city_name") var cityName: String,
    @ColumnInfo(name = "country_code") var countryCode: String,
    @ColumnInfo(name = "country_name") var countryName: String,
    @ColumnInfo(name = "zip_code") var zipCode: String
)