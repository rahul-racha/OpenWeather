package com.rahul.weatherapp.ui.LocationForecast

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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.rahul.weatherapp.R
import com.rahul.weatherapp.ui.ForcastParentAdapter
import com.rahul.weatherapp.ui.Locations.fragments.LocationsViewModel
import com.rahul.weatherapp.ui.LocationsAdapter
import com.rahul.weatherapp.ui.MainActivity
import com.rahul.weatherapp.ui.RecyclerItemSwipeHelper
import kotlinx.android.synthetic.main.forecast_fragment.view.*
import kotlinx.android.synthetic.main.locations_fragment.view.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class ForecastFragment : Fragment() {

    companion object {
        fun newInstance() = ForecastFragment()
    }

    private lateinit var viewModel: ForecastViewModel
    private lateinit var rootView: View
    private lateinit var recyclerView: RecyclerView
    private lateinit var localityView: TextView
    private lateinit var areaView: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.forecast_fragment, container, false)
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        setToolbarTitle(toolbar)
        recyclerView = rootView.parent_recycler_view
        localityView = rootView.locality_text_view_forecast
        areaView = rootView.area_text_view_forecast
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ForecastViewModel::class.java)

        setupRecyclerView()

        arguments?.let {
            val safeArgs = ForecastFragmentArgs.fromBundle(it)
            Log.e("FORECAST_DATA_RECEIVED", safeArgs.forecastData.toString())
            viewModel.setActionData(safeArgs.forecastData, safeArgs.locationViewData)
            setTextViews(safeArgs.locationViewData)
        }

        viewModel.forecastLiveData().observe(viewLifecycleOwner, Observer {
            it?.let { render(it) }
        })

    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        recyclerView.adapter = ForcastParentAdapter(viewModel.getParentList())
    }

    private fun setTextViews(viewData: LocationsViewModel.ViewData) {
        localityView.text = viewData.location.cityName
        val state = viewData.location.administrativeAreaLevel1
        areaView.text = viewData.location.countryCode
        if (state != "") {
            areaView.text = state + ", " + areaView.text
        }
        val zip = viewData.location.zipCode
        if (zip != "") {
            areaView.text = areaView.text.toString() + ", " + zip
        }
    }

    private fun render(viewState: ForecastViewModel.ViewState) {
        when (viewState.isLoading) {
            true -> (activity as MainActivity).setProgressBar(View.VISIBLE)
            false -> (activity as MainActivity).setProgressBar(View.INVISIBLE)
        }

        viewState.modelData?.let {
            recyclerView.adapter!!.notifyDataSetChanged()
        }
    }

    private fun setToolbarTitle(toolbar: Toolbar) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val titleString = getString(R.string.forecast_title)
            val modifyPart = getString(R.string.forecast_fTitle)
            val spannableContent = SpannableString(titleString)
            spannableContent.setSpan(
                ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.holo_red_light)),
                0,
                modifyPart.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            spannableContent.setSpan(
                StyleSpan(Typeface.DEFAULT.style),
                modifyPart.length,
                titleString.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            toolbar.title = spannableContent
        }
    }
}


//rootView.close_forecast.setOnClickListener(object : View.OnClickListener {
//    override fun onClick(v: View?) {
//        val loactionsAction = ForecastFragmentDirections.locationsAction()
////                Navigation.findNavController(rootView).navigate(loactionsAction)
////                val view = activity!!.findViewById<View>(R.id.nav_host_fragment)
////                Navigation.findNavController(view).popBackStack(R.id.locations_fragment, false)
//        val navController = (activity!! as MainActivity).navController
//        if (navController.currentDestination!!.id == R.id.forecast_fragment) {
//            navController.navigateUp() //popBackStack(R.id.locations_fragment, false)
//        }
//    }
//})