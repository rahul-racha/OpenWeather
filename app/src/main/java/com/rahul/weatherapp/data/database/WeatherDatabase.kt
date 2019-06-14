package com.rahul.weatherapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.rahul.weatherapp.data.database.entity.Location

@Database (
    entities = [Location::class],
    version = 1
)

abstract class WeatherDatabase: RoomDatabase() {
    abstract fun locationDao(): LocationDao

    companion object {
        @Volatile
        private var instance: WeatherDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context.applicationContext,
            WeatherDatabase::class.java, "forecast.db")
            .build()

    }
}