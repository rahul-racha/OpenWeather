package com.rahul.weatherapp.ui.Locations.fragments

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Build
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.rahul.weatherapp.R
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.rahul.weatherapp.data.network.ConnectivityInterceptor
import com.rahul.weatherapp.data.network.ConnectivityInterceptorImpl
import com.rahul.weatherapp.data.network.OpenWeatherAPIService
import com.rahul.weatherapp.internal.NoConnectivityException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.preference.PreferenceManager
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rahul.weatherapp.ui.AddPlaceActivity
import com.rahul.weatherapp.ui.LocationsAdapter
import com.rahul.weatherapp.ui.RecyclerItemTouchHelper
import com.rahul.weatherapp.ui.RecyclerItemTouchListener
import kotlinx.android.synthetic.main.locations_fragment.view.*


class LocationsFragment : Fragment(), RecyclerItemTouchListener {

    companion object {
        fun newInstance() = LocationsFragment()
    }

    private lateinit var viewModel: LocationsViewModel
    private lateinit var floatingButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.locations_fragment, container, false)
        floatingButton = rootView.fab
        recyclerView = rootView.recycler_view
        progressBar = rootView.progress_bar
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        setToolbarTitle(toolbar)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LocationsViewModel::class.java)

        floatingButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(activity, AddPlaceActivity::class.java)
                startActivityForResult(intent, LocationsViewModel.ADD_PLACE_ACTIVITY_CODE)
            }
        })

        viewModel.viewStateLiveData.observe(viewLifecycleOwner, Observer {
            it?.let { render(it) }
        })

//        val apiService = OpenWeatherAPIService(ConnectivityInterceptorImpl(context!!))
//
//        GlobalScope.launch(Dispatchers.Main) {
//            try {
//                val rep = apiService.getLocationWeather("Wexford, us").await()
//                Log.d("Response", rep.weather.toString())
//            } catch (e: NoConnectivityException) {
//                Toast.makeText(context, "No internet in coroutine", Toast.LENGTH_SHORT).show()
//                Log.d("INTERNET", "No internet")
//            }
//        }
    }

    private fun render(viewState: LocationsViewModel.ViewState) {
        when (viewState.isLoading) {
            true ->  {
                progressBar.visibility = View.VISIBLE
            }
            false -> {
                progressBar.visibility = View.INVISIBLE
            }
        }

        when (viewState.populateRecyclerViewData) {
            true -> {
                setupRecyclerView()
            }
        }

        if (viewState.newViewDataPosition > -1) {
            recyclerView.adapter!!.notifyItemInserted(viewState.newViewDataPosition)
        }
    }

    private fun setupRecyclerView() {
//        val decoration = DividerItemDecoration(recyclerView.context,
//                    DividerItemDecoration.HORIZONTAL)
//                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
//                    decoration.setDrawable(activity!!.resources.getDrawable(R.drawable.divider, null))
//                }
//                recyclerView.addItemDecoration(decoration)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = LocationsAdapter(viewModel.getListViewData())
        val itemTouchHelperCallback = ItemTouchHelper(RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT,
            this))
        itemTouchHelperCallback.attachToRecyclerView(recyclerView)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        Log.e("RESULT_CODE_ADD_PLACE", resultCode.toString())
        if (requestCode == LocationsViewModel.ADD_PLACE_ACTIVITY_CODE && resultCode == Activity.RESULT_OK) {
            if (null == intentData) {
                Toast.makeText(
                    context,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            val selectedPlace = intentData!!.extras.getParcelable<Place>("selected_place_parcelable")
            Log.e("Place_RESULT", selectedPlace.toString())
            viewModel.addNewPlace(selectedPlace)
        }
    }

    private fun setToolbarTitle(toolbar: Toolbar) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val titleString = getString(R.string.app_title)
            val modifyPart = getString(R.string.fTitle)
            val spannableContent = SpannableString(titleString)
            spannableContent.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.holo_red_light)), 0, modifyPart.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableContent.setSpan(StyleSpan(Typeface.BOLD), modifyPart.length, titleString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            toolbar.title = spannableContent
        }
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is LocationsAdapter.LocationViewHolder) {
            val viewData = viewModel.getListViewData()
            val locality: String = viewData[viewHolder.adapterPosition].location.cityName
            val deletedItem = viewData[viewHolder.adapterPosition]
            val deletedIndex = viewHolder.adapterPosition
            viewModel.removeItemFromViewData(deletedIndex)
            recyclerView.adapter!!.notifyItemRemoved(deletedIndex)


        }
    }

}
