package com.rahul.weatherapp.data.database

import androidx.room.*

import com.rahul.weatherapp.data.database.entity.Location
import com.rahul.weatherapp.data.database.entity.TableProperties as tProp

@Dao
interface LocationDao {

    @Query("SELECT * FROM ${tProp.tableName}")
    fun getAllLocations(): List<Location>

    @Query("SELECT EXISTS(SELECT * FROM ${tProp.tableName} WHERE ${tProp.placeID} = :placeID)")
    fun isPlaceExists(placeID: String): Boolean

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg locations: Location)

    @Query("UPDATE ${tProp.tableName} SET ${tProp.placeID} = :placeID, ${tProp.cityID} = :cityID, ${tProp.cityName} = :cityName, ${tProp.state} = :state, ${tProp.countryCode} = :countryCode, ${tProp.countryName} = :countryName, ${tProp.zipCode} = :zipCode WHERE ${tProp.placeID} = :existingPlaceID")
    fun update(existingPlaceID: String, placeID: String, cityID: String, cityName: String, state: String, countryCode: String,
               countryName: String, zipCode: String)
    @Query("DELETE FROM ${tProp.tableName} WHERE ${tProp.placeID} = :placeID")
    fun delete(placeID: String)

    @Query("DELETE FROM ${tProp.tableName}")
    fun deleteAll()
}