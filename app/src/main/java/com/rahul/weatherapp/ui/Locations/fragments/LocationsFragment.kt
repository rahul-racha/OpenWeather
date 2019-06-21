package com.rahul.weatherapp.ui.Locations.fragments

import android.app.Activity
import android.app.AlertDialog
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
import android.widget.ImageView
import android.widget.Toast
import com.rahul.weatherapp.R
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.rahul.weatherapp.ui.*
import kotlinx.android.synthetic.main.location_row.view.*
import kotlinx.android.synthetic.main.locations_fragment.view.*


class LocationsFragment : Fragment(), RecyclerItemTouchListener {

    companion object {
        fun newInstance() = LocationsFragment()
    }

    private lateinit var viewModel: LocationsViewModel
    private lateinit var floatingButton: FloatingActionButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerItemTouchHelper: RecyclerItemSwipeHelper
    private lateinit var itemTouchHelperCallback: ItemTouchHelper
    private lateinit var rootView: View
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private lateinit var placeholderImageView: ImageView
    private lateinit var placeholderTextView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.locations_fragment, container, false)
        floatingButton = rootView.fab
        recyclerView = rootView.recycler_view
        swipeRefreshLayout = rootView.refresh_layout
        placeholderImageView = rootView.empty_locations_image_view
        placeholderTextView = rootView.empty_locations_text_view
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        setToolbarTitle(toolbar)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LocationsViewModel::class.java)
        setListeners()
        setupRecyclerView()

        viewModel.viewStateLiveData.observe(viewLifecycleOwner, Observer {
            it?.let { render(it) }
        })
    }

    private fun setListeners() {
        floatingButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(activity, AddPlaceActivity::class.java)
                intent.putExtra(R.string.hint_text_key.toString(), "Search for location or zip code")
                startActivityForResult(intent, LocationsViewModel.ADD_PLACE_ACTIVITY_CODE)
            }
        })

        swipeRefreshLayout.setOnRefreshListener(object : SwipeRefreshLayout.OnRefreshListener {
            override fun onRefresh() {
                viewModel.clearListViewData()
                viewModel.loadSavedLocations()
            }
        })
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = LocationsAdapter(viewModel.getListViewData(), this)
        recyclerItemTouchHelper = RecyclerItemSwipeHelper(0,ItemTouchHelper.LEFT,this)
        itemTouchHelperCallback = ItemTouchHelper(recyclerItemTouchHelper)
        itemTouchHelperCallback.attachToRecyclerView(recyclerView)
    }

    private fun render(viewState: LocationsViewModel.ViewState) {

        if (viewModel.getListViewData().isEmpty()) {
            recyclerView.visibility = View.INVISIBLE
            placeholderImageView.visibility = View.VISIBLE
            placeholderTextView.visibility = View.VISIBLE

        } else {
            recyclerView.visibility = View.VISIBLE
            placeholderImageView.visibility = View.INVISIBLE
            placeholderTextView.visibility = View.INVISIBLE
        }

        when (viewState.isLoading) {
            true ->  {
                if (!swipeRefreshLayout.isRefreshing) {
                    (activity as MainActivity).setProgressBar(View.VISIBLE)
                }
            }
            false -> {
                if (swipeRefreshLayout.isRefreshing) {
                    swipeRefreshLayout.isRefreshing = false
                }
                (activity as MainActivity).setProgressBar(View.INVISIBLE)
            }
        }

        when (viewState.populateRecyclerViewData) {
            true -> {
                recyclerView.adapter!!.notifyDataSetChanged()
            }
        }

        if (viewState.newViewDataPosition > -1) {
            recyclerView.adapter!!.notifyItemInserted(viewState.newViewDataPosition)
        }

        if (viewState.updateViewDataAtPosition > -1) {
            recyclerView.adapter!!.notifyItemChanged(viewState.updateViewDataAtPosition)
        }

        if (viewState.forecastData != null) {
            Log.e("ON_CLICK_VIEW_STATE", viewState.toString())
            val viewData = viewModel.getListViewData()[viewState.forecastData.position]
            val forecastAction = LocationsFragmentDirections.forecastAction(viewState.forecastData.forcastResponse,
                viewData)
            val navController =  (activity!! as MainActivity).navController
            viewModel.resetForecastState()
            navController.popBackStack(R.id.locations_fragment, false)
            if (navController.currentDestination!!.id == R.id.locations_fragment) {
                navController.navigate(forecastAction)
            }
        }

        viewState.messageBox?.let {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(it.title)
            builder.setMessage(it.message)
            builder.setPositiveButton("OK", null)
            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intentData: Intent?) {
        super.onActivityResult(requestCode, resultCode, intentData)
        Log.e("RESULT_CODE_ADD_PLACE", resultCode.toString())

        if (resultCode == Activity.RESULT_OK) {
            if (null == intentData) {
                Toast.makeText(
                    context,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
                ).show()
                return
            }
            val selectedPlace = intentData!!.extras.getParcelable<Place>("selected_place_parcelable")
            Log.e("PLACE_RESULT", selectedPlace.toString())
            if (selectedPlace != null) {
                if (requestCode == LocationsViewModel.ADD_PLACE_ACTIVITY_CODE) {
                    viewModel.loadNewPlace(selectedPlace!!)
                    return
                }
                if (requestCode == LocationsViewModel.EDIT_PLACE_ACTIVITY_CODE) {
                    Log.e("ACTIVTY_RESULT_POSITION", R.string.edit_place_key.toString())
                    viewModel.replaceItem(intentData.extras.getInt(R.string.edit_place_key.toString()), selectedPlace!!)
                    return
                }
            }
            //Throw Alert Box
            Toast.makeText(context, R.string.place_not_saved, Toast.LENGTH_LONG).show()
            return
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
        //Mark: Can design a custom swipe feature on recycler view registering click events. Painful :(
        if (viewHolder is LocationsAdapter.LocationViewHolder) {
            val viewData = viewModel.getListViewData()
            val locality: String = viewData[position].location.cityName
            val deletedItem = viewData[position]
            viewModel.removeItemFromViewData(position)
            viewModel.addDeletedLocationToMap(deletedItem)
            recyclerView.adapter!!.notifyItemRemoved(position)

            val snackbar = Snackbar.make(rootView, locality + " removed from locations", Snackbar.LENGTH_LONG)
            snackbar.setDuration(6000)
            snackbar.setAction("UNDO", object : View.OnClickListener {
                override fun onClick(v: View?) {
                    viewModel.restoreLocation(position, deletedItem)
                    Log.e("UNDO_ITEM",deletedItem.location.placeID.toString())
                    recyclerView.adapter!!.notifyItemInserted(position)
                }
            })

            snackbar.addCallback(object : Snackbar.Callback() {
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    Log.e("SNACKBAR_DISMISSED", "Dismissed at postion: ${position}")
                    viewModel.clearLocationsFromMap()
                }
            })

            snackbar.setActionTextColor(Color.YELLOW)
            snackbar.show()
        }
    }

    override fun onClicked(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is LocationsAdapter.LocationViewHolder) {
            viewModel.fetchForecastForLocation(position)
        }
    }

    override fun onClicked(view: View, position: Int) {
        // Mark: Observe for view
    }

    override fun onEditClicked(viewHolder: RecyclerView.ViewHolder, position: Int) {
        if (viewHolder is LocationsAdapter.LocationViewHolder) {
            val intent = Intent(activity, AddPlaceActivity::class.java)
            var hintText = "Edit " + (viewHolder.itemView.view_foreground.locality_textview.text as String) // + ", " +
//                    (viewHolder.itemView.view_foreground.area_textview.text as String)
            val zipCode = viewModel.getListViewData()[position].location.zipCode
            if (zipCode != "") {
                hintText += ", " + zipCode
            }
            intent.putExtra(R.string.hint_text_key.toString(), hintText)
            intent.putExtra(R.string.edit_place_key.toString(), position)
            startActivityForResult(intent, LocationsViewModel.EDIT_PLACE_ACTIVITY_CODE)
        }
    }

}
