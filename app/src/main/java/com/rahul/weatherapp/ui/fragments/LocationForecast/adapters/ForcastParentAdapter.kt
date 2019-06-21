package com.rahul.weatherapp.ui.fragments.LocationForecast.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.rahul.weatherapp.R
import com.rahul.weatherapp.ui.fragments.LocationForecast.ForecastParentModel
import kotlinx.android.synthetic.main.forecast_parent_row.view.*

class ForcastParentAdapter(val parentList: List<ForecastParentModel>):
    RecyclerView.Adapter<ForcastParentAdapter.ForecastParentViewHolder>() {

    private val viewPool = RecyclerView.RecycledViewPool()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ForecastParentViewHolder {
        return ForecastParentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.forecast_parent_row, parent, false)
        )
    }

    override fun getItemCount() = parentList.size

    override fun onBindViewHolder(holder: ForecastParentViewHolder, position: Int) {
        val parent = parentList[position]
        holder.view.parent_day.text = parent.day
        holder.view.parent_date.text = parent.date
        val childLayoutManager = LinearLayoutManager(holder.view.child_recycler_view.context, RecyclerView.HORIZONTAL,
            false)
        childLayoutManager.initialPrefetchItemCount = 3

        holder.view.child_recycler_view.apply {
            layoutManager = childLayoutManager
            adapter = ForecastChildAdapter(parent.childList)
            setRecycledViewPool(viewPool)
        }
    }

    class ForecastParentViewHolder(val view: View) : RecyclerView.ViewHolder(view)
}