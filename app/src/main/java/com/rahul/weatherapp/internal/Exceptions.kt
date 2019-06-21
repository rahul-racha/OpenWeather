package com.rahul.weatherapp.internal

import java.io.IOException

class NoConnectivityException: IOException()

data class MessageBox(val title: String, val message: String)