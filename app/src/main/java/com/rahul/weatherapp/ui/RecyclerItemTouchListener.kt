package com.rahul.weatherapp.ui

import androidx.recyclerview.widget.RecyclerView

interface RecyclerItemTouchListener {
    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
}