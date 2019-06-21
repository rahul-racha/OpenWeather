package com.rahul.weatherapp.ui.fragments.LocationForecast.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.rahul.weatherapp.R
import com.rahul.weatherapp.data.network.LocationForecastResponse.Forecast
import kotlinx.android.synthetic.main.forecast_child_row.view.*
import kotlin.math.roundToInt

class ForecastChildAdapter(val childList: List<Forecast>):
    RecyclerView.Adapter<ForecastChildAdapter.ForcastChildViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForcastChildViewHolder {
        return ForcastChildViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.forecast_child_row, parent, false)
        )
    }

    override fun getItemCount() = childList.size

    override fun onBindViewHolder(holder: ForcastChildViewHolder, position: Int) {
        val forcast = childList[position]
        holder.view.child_time.text = forcast.customTime ?: "-"
        val temperature = forcast!!.main!!.temp!!.roundToInt()
        holder.view.child_temp_text_view.text =  temperature.toString() + "°F"
        val windSpeed = forcast!!.wind!!.speed!!.roundToInt()
        holder.view.child_wind_text_view.text = windSpeed.toString() + "mph"
        holder.view.child_weather_description.text = forcast!!.weather!![0]!!.description as String

        val icon = forcast!!.weather!![0]!!.icon
        val imageURL = "http://openweathermap.org/img/w/" + icon + ".png"
        Glide.with(holder.view.context)
            .load(imageURL)
            .into(holder.view.child_weather_logo)
    }

    class ForcastChildViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}