package com.rahul.weatherapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ListView
import androidx.navigation.NavController
import com.rahul.weatherapp.R
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import kotlinx.android.synthetic.main.location_row.view.*
import android.content.Intent
import android.transition.Visibility
import android.util.Log
import androidx.fragment.app.Fragment
import com.rahul.weatherapp.ui.Locations.fragments.LocationsFragment
import android.view.View
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import com.google.android.material.floatingactionbutton.FloatingActionButton


class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var locationsListView: ListView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        progressBar = findViewById(R.id.progress_bar_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        setViewElements()
        setNavController()
    }

    private fun setViewElements() {
//        locationsListView = findViewById<ListView>(R.id.locations_listView)
//        locationsListView.adapter = LocationsAdapter(context = this)
    }

    private fun setNavController() {
        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, null)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Log.e("MAIN_ACT_CODE_ADD_PLACE", resultCode.toString())
    }

    fun setProgressBar(visibility: Int) {
        progressBar.visibility = visibility
    }


//    private class LocationsAdapter(context: Context): BaseAdapter() {
//
//        private val mContext: Context
//
//        init {
//            mContext = context
//        }
//
//        override fun getCount(): Int {
//            return 10
//        }
//
//        override fun getItemId(position: Int): Long {
//            return position.toLong()
//        }
//
//        override fun getItem(position: Int): Any {
//            return "private String" //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun getView(p0: Int, convertView: View?, viewGroup: ViewGroup?): View {
//            val locRow: View
//            if (convertView == null) {
//                val layoutInflater = LayoutInflater.from(mContext)
//                locRow = layoutInflater.inflate(R.layout.location_row, viewGroup, false)
//                val logoImageView = locRow.locLogoImageView
////                val logoImageView = locRow.findViewById<ImageView>(R.id.locLogoImageView)
//                val viewHolder = ViewHolder(logo = logoImageView)
//                locRow.tag = viewHolder
//            } else {
//                locRow = convertView
//            }
//            val viewHolder = locRow.tag as ViewHolder
////            viewHolder.logo =
//            return locRow
//        }
//
//        private class ViewHolder(val logo: ImageView)
//    }
}
