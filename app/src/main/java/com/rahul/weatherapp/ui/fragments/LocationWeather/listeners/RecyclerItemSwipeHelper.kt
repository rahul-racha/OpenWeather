package com.rahul.weatherapp.ui.fragments.LocationWeather.listeners

import android.graphics.Canvas
import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.rahul.weatherapp.ui.fragments.LocationWeather.adapters.LocationsAdapter
import kotlinx.android.synthetic.main.location_row.view.*



class RecyclerItemSwipeHelper(dragDirs: Int, swipeDirs: Int, listener: RecyclerItemTouchListener) :
    ItemTouchHelper.SimpleCallback(dragDirs, swipeDirs) {

    private var listener: RecyclerItemTouchListener
    var swipedViewHolder: RecyclerView.ViewHolder? = null

    init {
        this.listener = listener
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        listener.onSwiped(viewHolder, direction, viewHolder.adapterPosition)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        val foregroundView: View = (viewHolder as LocationsAdapter.LocationViewHolder).view.view_foreground
        ItemTouchHelper.Callback.getDefaultUIUtil().clearView(foregroundView)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val foregroundView: View = (viewHolder as LocationsAdapter.LocationViewHolder).view.view_foreground
        ItemTouchHelper.Callback.getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState,
            isCurrentlyActive)

    }

    override fun onChildDrawOver(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder?,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val foregroundView: View = (viewHolder as LocationsAdapter.LocationViewHolder).view.view_foreground
        ItemTouchHelper.Callback.getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState,
            isCurrentlyActive)
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
        viewHolder?.let {
            val foregroundView: View = (it as LocationsAdapter.LocationViewHolder).view.view_foreground
            ItemTouchHelper.Callback.getDefaultUIUtil().onSelected(foregroundView)
        }
    }



}