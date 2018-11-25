package com.chitakpatel.eventcalender

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.chintanpatel.materialeventcalendar.CalenderView
import com.chintanpatel.materialeventcalendar.EventItem
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val eventList: ArrayList<EventItem> = arrayListOf()

        eventList.add(
            EventItem("21-11-2018", "29-11-2018", "Gujarat Trip", "#EEFFCC", "1")
        )
        eventList.add(
            EventItem("30-11-2018", "02-12-2018", "Meeting")
        )

        eventCalendar.setCalenderEventClickListener(object : CalenderView.CalenderEventClickListener {
            override fun onEventClick(eventItem: EventItem) {
                //put your logic on event click
            }
        })

        eventCalendar.addEventList(eventList)

        eventCalendar.addEvent(
            EventItem("05-12-2018", "07-12-2018")
        )
    }
}
