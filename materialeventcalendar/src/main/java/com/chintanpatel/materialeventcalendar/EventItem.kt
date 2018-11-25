package com.chintanpatel.materialeventcalendar

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by chintak on 29/9/17.
 */
data class EventItem(var start: String, var end: String, var title: String = "My Event", var color: String = "#00AFFF", var id: String = "") {
    fun getStartDateToSort(): Date {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.parse(start)
    }
}