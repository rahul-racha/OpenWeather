package com.rahul.weatherapp.data.database

import androidx.room.*
import androidx.lifecycle.LiveData

import com.rahul.weatherapp.data.database.entity.Location
import com.rahul.weatherapp.data.database.entity.TableProperties as tProp

@Dao
interface LocationDao {

    @Query("SELECT * FROM ${tProp.tableName} ORDER BY ${tProp.filterASC} ASC")
    fun getAllLocations(): LiveData<List<Location>>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    fun insert(vararg locations: Location)

    @Delete
    fun delete(vararg locations: Location)

    @Query("DELETE FROM ${tProp.tableName}")
    fun deleteAll()
}