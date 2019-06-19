package com.rahul.weatherapp.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView

interface RecyclerItemTouchListener {
    fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int)
    fun onClicked(view: View, position: Int)
    fun onDeleteClicked(position: Int)
    fun onEditClicked(position: Int)
}