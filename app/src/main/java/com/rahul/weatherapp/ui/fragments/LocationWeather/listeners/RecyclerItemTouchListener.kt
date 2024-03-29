package com.rahul.weatherapp.ui.fragments.LocationWeather.listeners

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface RecyclerItemTouchListener {
    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
    fun onClicked(viewHolder: RecyclerView.ViewHolder, position: Int)
    fun onClicked(view: View, position: Int)
//    fun onDeleteClicked(position: Int)
    fun onEditClicked(viewHolder: RecyclerView.ViewHolder, position: Int)
}