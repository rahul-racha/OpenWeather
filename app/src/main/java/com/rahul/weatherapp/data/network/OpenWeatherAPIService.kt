package com.rahul.weatherapp.data.network

import android.util.Log
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.rahul.weatherapp.data.network.LocationForecastResponse.LocationForecastResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse
import com.rahul.weatherapp.data.network.LocationWeatherResponse.BulkLocationWeatherResponse
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
//fun getCountryCode(countryName: String) =
//    Locale.getISOCountries().find { Locale("", it).displayCountry == countryName }
const val APP_ID = "22688e4d3dabcc703683aa5720c556f4"

//api.openweathermap.org/data/2.5/weather?zip=15090&APPID=22688e4d3dabcc703683aa5720c556f4

interface OpenWeatherAPIService {
    @GET(value = "weather")
    fun getLocationWeatherByZip(
        @Query(value = "zip") zipAndCountryCode: String,
        @Query(value = "units") units: String = "imperial"
    ): Deferred<LocationWeatherResponse>

    @GET(value = "weather")
    fun getLocationWeatherByCity(
        @Query(value = "q") cityAndCountryCode: String,
        @Query(value = "units") units: String = "imperial"
    ): Deferred<LocationWeatherResponse>

    @GET(value = "group")
    fun getLocationWeatherInBulk(
        @Query(value = "id") cityIDs: String,
        @Query(value = "units") units: String = "imperial"
    ): Deferred<BulkLocationWeatherResponse>

    @GET(value = "forecast")
    fun getForecast(
        @Query(value = "id") cityID: String,
        @Query(value = "units") units: String = "imperial"
    ): Deferred<LocationForecastResponse>

    companion object {
        operator fun invoke(
            connectivityInterceptor: ConnectivityInterceptor
        ): OpenWeatherAPIService {
            val requestInterceptor = Interceptor { chain ->
                val newUrl = chain.request()
                    .url()
                    .newBuilder()
                    .addQueryParameter("APPID", APP_ID)
                    .build()
                Log.e("REQUEST_URL",newUrl.toString())
                val request = chain.request()
                    .newBuilder()
                    .url(newUrl)
                    .build()

                return@Interceptor chain.proceed(request)
            }

            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor(requestInterceptor)
                .addInterceptor(connectivityInterceptor)
                .build()

            return Retrofit.Builder()
                .client(okHttpClient)
                .baseUrl("https://api.openweathermap.org/data/2.5/")
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(OpenWeatherAPIService::class.java)

        }
    }
}

