package com.rahul.weatherapp.ui

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahul.weatherapp.R
import com.rahul.weatherapp.ui.Locations.fragments.LocationsViewModel
import kotlinx.android.synthetic.main.location_row.view.*

class LocationsAdapter(val locationWeatherList : List<LocationsViewModel.ViewData>, val listener: RecyclerItemTouchListener) :
    RecyclerView.Adapter<LocationsAdapter.LocationViewHolder>() {


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

        holder.view.zip_card_view.visibility = View.INVISIBLE
        if (locationWeather.location.zipCode != "") {
            holder.view.area_textview.text = (holder.view.area_textview.text as String) + ", " +
                    locationWeather.location.zipCode
        }
        holder.view.temp_textview.text = locationWeather.weather.main!!.temp!!.toString() + "°F"
        holder.view.description_textview.text = locationWeather.weather.weather!![0]!!.description
        val icon = locationWeather.weather.weather!![0]!!.icon
        val imageURL = "http://openweathermap.org/img/w/" + icon + ".png"
        Glide.with(holder.view.context)
            .load(imageURL)
            .into(holder.view.weather_logo)

        holder.view.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                listener.onClicked(holder, holder.adapterPosition)
            }
        })

        holder.view.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                listener.onEditClicked(holder, holder.adapterPosition)
                return true
            }
        })
    }

    class LocationViewHolder(val view: View) : RecyclerView.ViewHolder(view)

}