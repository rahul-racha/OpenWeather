package com.rahul.weatherapp.data.network

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.rahul.weatherapp.data.network.LocationWeatherResponse.LocationWeatherResponse
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
    fun getLocationWeather(
        @Query(value = "q") cityName: String
    ): Deferred<LocationWeatherResponse>

    @GET(value = "weather")
    fun getLocationWeatherByID(
        @Query(value = "id") cityID: String
    ): Deferred<LocationWeatherResponse>

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

