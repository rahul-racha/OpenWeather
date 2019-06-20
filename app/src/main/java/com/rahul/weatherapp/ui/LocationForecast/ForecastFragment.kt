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
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController

import com.rahul.weatherapp.R
import com.rahul.weatherapp.ui.MainActivity
import kotlinx.android.synthetic.main.forecast_fragment.view.*

class ForecastFragment : Fragment() {

    companion object {
        fun newInstance() = ForecastFragment()
    }

    private lateinit var viewModel: ForecastViewModel
    private lateinit var rootView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        rootView = inflater.inflate(R.layout.forecast_fragment, container, false)
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        setToolbarTitle(toolbar)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(ForecastViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootView.close_forecast.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val loactionsAction = ForecastFragmentDirections.locationsAction()
//                Navigation.findNavController(rootView).navigate(loactionsAction)
//                val view = activity!!.findViewById<View>(R.id.nav_host_fragment)
//                Navigation.findNavController(view).popBackStack(R.id.locations_fragment, false)
                val navController = (activity!! as MainActivity).navController
                if (navController.currentDestination!!.id == R.id.forecast_fragment) {
                    navController.navigateUp() //popBackStack(R.id.locations_fragment, false)
                }
            }
        })

        arguments?.let {
            val safeArgs = ForecastFragmentArgs.fromBundle(it)
            Log.e("FORECAST_DATA_RECEIVED", safeArgs.forecastData.toString())
        }
    }

    private fun setToolbarTitle(toolbar: Toolbar) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val titleString = getString(R.string.forecast_title)
            val modifyPart = getString(R.string.forecast_fTitle)
            val spannableContent = SpannableString(titleString)
            spannableContent.setSpan(ForegroundColorSpan(ContextCompat.getColor(context!!, R.color.holo_red_light)), 0, modifyPart.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableContent.setSpan(StyleSpan(Typeface.DEFAULT.style), modifyPart.length, titleString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            toolbar.title = spannableContent
        }
    }


}
