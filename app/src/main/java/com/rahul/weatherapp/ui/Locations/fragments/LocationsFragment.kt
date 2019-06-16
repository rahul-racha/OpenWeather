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
import com.rahul.weatherapp.data.network.ConnectivityInterceptor
import com.rahul.weatherapp.data.network.ConnectivityInterceptorImpl
import com.rahul.weatherapp.data.network.OpenWeatherAPIService
import com.rahul.weatherapp.internal.NoConnectivityException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.preference.PreferenceManager
import androidx.core.content.ContextCompat
import com.google.android.libraries.places.api.model.Place
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.rahul.weatherapp.ui.AddPlaceActivity
import kotlinx.android.synthetic.main.locations_fragment.view.*


class LocationsFragment : Fragment() {

    companion object {
        fun newInstance() = LocationsFragment()
    }

    private lateinit var viewModel: LocationsViewModel
    private lateinit var floatingButton: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.locations_fragment, container, false)
        floatingButton = rootView.fab
        val toolbar: Toolbar = activity!!.findViewById(R.id.toolbar)
        setToolbarTitle(toolbar)
        return rootView
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(LocationsViewModel::class.java)
        // TODO: Use the ViewModel
        handleDefaultPreferences()
        viewModel.loadSavedLocations()

        floatingButton.setOnClickListener(object: View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(activity, AddPlaceActivity::class.java)
                startActivityForResult(intent, LocationsViewModel.ADD_PLACE_ACTIVITY_CODE)
            }
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
//          intentData?.let { data ->
            val selectedPlace = intentData!!.extras.getParcelable<Place>("selected_place_parcelable")
            Log.e("Place_RESULT", selectedPlace.toString())
//          }
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

    private fun handleDefaultPreferences() {
        PreferenceManager.getDefaultSharedPreferences(activity!!).apply {
            if (!getBoolean(LocationsViewModel.FIRST_LAUNCH_COMPLETED, false)) {
                viewModel.initLocationDatabase()
                edit().apply {
                    putBoolean(LocationsViewModel.FIRST_LAUNCH_COMPLETED, true)
                    apply()
                }
            }
        }
    }

}
