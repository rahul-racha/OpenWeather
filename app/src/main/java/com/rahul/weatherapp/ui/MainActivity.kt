package com.rahul.weatherapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.navigation.NavController
import com.rahul.weatherapp.R

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var locationsListView: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        setViewElements()
    }

    private fun setViewElements() {
        locationsListView = findViewById<ListView>(R.id.locations_listView)
//        locationsListView.adapter = LocationsAdapter()
    }

//    override fun onSupportNavigateUp(): Boolean {
//        return super.onSupportNavigateUp(drawerLayout: null, navController)
//    }


//    private class LocationsAdapter: BaseAdapter() {
//
//        override fun getCount(): Int {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun getItemId(position: Int): Long {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun getItem(position: Int): Any {
//            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
//        }
//
//        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
//
//        }
//    }
}
