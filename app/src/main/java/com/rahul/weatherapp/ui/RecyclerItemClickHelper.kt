package com.rahul.weatherapp.ui

import android.content.Context
import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.locations_fragment.view.*

class RecyclerItemClickHelper(context: Context, recyclerView: RecyclerView, listener: RecyclerItemTouchListener):
    RecyclerView.OnItemTouchListener {
    private var listener: RecyclerItemTouchListener

    init {
        this.listener = listener
    }

    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val itemView = rv.findChildViewUnder(e.x, e.y)
        itemView?.let {
            listener.onClicked(itemView, rv.getChildAdapterPosition(it))
            return true
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
        // Mark: observe
    }

    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
        // Mark: observe
    }
}