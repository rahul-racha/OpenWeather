package com.rahul.weatherapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahul.weatherapp.R
import com.rahul.weatherapp.ui.Locations.fragments.LocationsViewModel
import kotlinx.android.synthetic.main.location_row.view.*

class LocationsAdapter(val locationWeatherList : List<LocationsViewModel.ViewData>) : RecyclerView.Adapter<LocationsAdapter.LocationViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LocationViewHolder {
        return LocationViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.location_row, parent, false)
        )
    }

    override fun getItemCount() = locationWeatherList.size

    override fun onBindViewHolder(holder: LocationViewHolder, position: Int) {
        val locationWeather = locationWeatherList[position]
        holder.view.locality_textview.text = locationWeather.location.cityName
        if (locationWeather.location.administrativeAreaLevel1 != "") {
            holder.view.area_textview.text = locationWeather.location.administrativeAreaLevel1 + ", " +
                    locationWeather.location.countryCode
        } else {
            holder.view.area_textview.text = locationWeather.location.countryCode
        }
        holder.view.temp_textview.text = locationWeather.weather.main!!.temp!!.toString() + "°F"
//        if (locationWeather.location.zipCode == "") {
//            holder.view.zip_card_view.visibility = View.INVISIBLE
//            holder.view.zip_textview.visibility = View.INVISIBLE
//        } else {
//            holder.view.zip_card_view.visibility = View.VISIBLE
//            holder.view.zip_textview.visibility = View.VISIBLE
//            holder.view.zip_textview.text = locationWeather.location.zipCode
//        }
        holder.view.zip_textview.text = "22305"
        holder.view.description_textview.text = locationWeather.weather.weather!![0]!!.description
        val icon = locationWeather.weather.weather!![0]!!.icon
        val imageURL = "http://openweathermap.org/img/w/" + icon + ".png"
        Glide.with(holder.view.context)
            .load(imageURL)
            .into(holder.view.weather_logo)
    }


    class LocationViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}