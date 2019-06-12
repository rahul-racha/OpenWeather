package com.rahul.weatherapp.ui.Locations.fragments

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
import com.rahul.weatherapp.R
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.rahul.weatherapp.data.OpenWeatherAPIService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class  LocationsFragment : Fragment() {

    companion object {
        fun newInstance() = LocationsFragment()
    }

    private lateinit var viewModel: LocationsViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.locations_fragment, container, false)
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        if (null != toolbar) {
//            (activity as AppCompatActivity).setSupportActionBar(toolbar)
            setToolbarTitle(toolbar)
        }
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LocationsViewModel::class.java)
        // TODO: Use the ViewModel
        val apiService = OpenWeatherAPIService()

        GlobalScope.launch(Dispatchers.Main) {
            val rep = apiService.getLocationWeather("Wexford, us").await()
            Log.d("Response", rep.weather.toString())
        }
    }

    private fun setToolbarTitle(toolbar: Toolbar) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val titleString = getString(R.string.app_title)
            val modifyPart = getString(R.string.fTitle)
            val spannableContent = SpannableString(titleString)
            spannableContent.setSpan(ForegroundColorSpan(Color.RED), 0, modifyPart.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableContent.setSpan(StyleSpan(Typeface.BOLD), modifyPart.length, titleString.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            toolbar.title = spannableContent
        }
    }

}
