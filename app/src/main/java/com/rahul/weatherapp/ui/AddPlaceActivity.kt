package com.rahul.weatherapp.ui

import android.app.Activity
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.rahul.weatherapp.R
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.api.Places;
import android.util.Log
import android.view.View
import android.widget.Button
import com.google.android.libraries.places.api.model.TypeFilter
import java.lang.Exception


class AddPlaceActivity : AppCompatActivity() {

    private val MAPS_API_KEY = "AIzaSyAx4Gy3Dv_268GwQKUJviuS4MOQOvSrnYs"
    private lateinit var autocompleteFragment: AutocompleteSupportFragment
    private lateinit var placesClient: PlacesClient
    private var selectedPlace: Place? = null
    private lateinit var cancelButton: Button
    private lateinit var saveButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_place)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        setToolbarTitle(toolbar)
        initSubViews()
        configurePlaceListener()
        addListeners()
    }

    private fun setToolbarTitle(toolbar: Toolbar) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            val titleString = getString(R.string.app_title)
            val modifyPart = getString(R.string.fTitle)
            val spannableContent = SpannableString(titleString)
            spannableContent.setSpan(ForegroundColorSpan(ContextCompat.getColor(baseContext, R.color.holo_red_light)),
                0, modifyPart.length, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            spannableContent.setSpan(StyleSpan(Typeface.BOLD), modifyPart.length, titleString.length,
                Spannable.SPAN_INCLUSIVE_INCLUSIVE)
            toolbar.title = spannableContent
        }
    }

    private fun initSubViews() {
        cancelButton = findViewById(R.id.cancel_button)
        saveButton = findViewById(R.id.save_button)
        Places.initialize(applicationContext, MAPS_API_KEY)
        placesClient = Places.createClient(this)
        autocompleteFragment = supportFragmentManager.findFragmentById(R.id.autocomplete_fragment)
                as AutocompleteSupportFragment
    }

    private fun addListeners() {
        addPlaceSelectionListener()

        cancelButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                setResult(Activity.RESULT_CANCELED)
                finish()
            }
        })

        saveButton.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intentWithResult = Intent()
                if (selectedPlace == null) {
                    setResult(Activity.RESULT_OK)
                } else {
                    intentWithResult.putExtra("selected_place_parcelable", selectedPlace)
                    val editPosition = intent.extras.getInt(R.string.edit_place_key.toString())
                    Log.e("EDIT_CLICKED_POSITION", editPosition.toString())
                    if (editPosition != null) {
                        intentWithResult.putExtra(R.string.edit_place_key.toString(), editPosition)
                    }
                    setResult(Activity.RESULT_OK, intentWithResult)
                }
                finish()
            }
        })
    }

    private fun configurePlaceListener() {
        autocompleteFragment.setPlaceFields(mutableListOf(Place.Field.ID, Place.Field.NAME,
            Place.Field.ADDRESS_COMPONENTS, Place.Field.PHOTO_METADATAS))
        autocompleteFragment.setTypeFilter(TypeFilter.REGIONS)
        val hintText = intent.getStringExtra(R.string.hint_text_key.toString())
        Log.e("HINT_TEXT", hintText)
        if (hintText != null) {
            autocompleteFragment.setHint(hintText)
        }
    }

    private fun addPlaceSelectionListener() {
        autocompleteFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                selectedPlace = place
                Log.i(
                    "LISTENER_RESULT", "Place: " + place.name + ", " + place.addressComponents + "," +
                            place.photoMetadatas
                )
            }

            override fun onError(status: Status) {
                Log.i("LISTENER_FAIL", "An error occurred: $status")
            }
        })
    }
}
