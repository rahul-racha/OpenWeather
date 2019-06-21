package com.rahul.weatherapp.internal

import android.os.Build
import androidx.lifecycle.LiveData
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*

fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
    observe(lifecycleOwner, object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun <T> LiveData<T>.observeOnceForever(observer: Observer<T>) {
    observeForever(object : Observer<T> {
        override fun onChanged(t: T?) {
            observer.onChanged(t)
            removeObserver(this)
        }
    })
}

fun getParsedDateString(dateText: String, formatPattern: String = "yyyy-MM-dd", oldFormatter: SimpleDateFormat): String {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val date: LocalDate = LocalDate.parse(dateText)
        val formatter = DateTimeFormatter.ofPattern(formatPattern)
        return date.format(formatter)
    } else {
        val date = oldFormatter.parse(dateText)
        val targetDateFormat = SimpleDateFormat(formatPattern)
        return targetDateFormat.format(date)
    }
}

fun getDayOfWeek(dateText: String, pattern: String = "EEEE",
                 oldFormatter: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd")): String {
//    val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//        LocalDate.parse(dateText)
//    } else {
//
//    }
    val date = oldFormatter.parse(dateText)
    val sdf = SimpleDateFormat(pattern)
    return sdf.format(date)
}