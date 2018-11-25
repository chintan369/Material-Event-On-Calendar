package com.chintanpatel.materialeventcalendar

//import it.sephiroth.android.library.tooltip.Tooltip
import android.content.Context
import android.graphics.Color
import android.support.v7.widget.CardView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.nex3z.flowlayout.FlowLayout
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList


/**
 * Created by chintak on 28/9/17.
 */
class CalenderView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr) {

    var MAX_EVENT = 3
    var eventClickListener: CalenderEventClickListener? = null

    companion object {
        const val monthYearFormat = "MMM yyyy"
        const val dateFormat = "dd-MM-yyyy"
        const val dateTimeFormat = "dd-MM-yyyy hh:mm:ss"
        const val EXTRA_MARGIN = 15
        const val POST_TIME: Long = 100
    }

    private var eventList: ArrayList<EventModal> = ArrayList()
    private var calender: Calendar = Calendar.getInstance()
    private var layoutInflater: LayoutInflater =
        context?.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater


    private var dayViewHeader: View = layoutInflater.inflate(R.layout.layout_day_header_view, this, false)
    private var dayViewRow1: FrameLayout =
        layoutInflater.inflate(R.layout.layout_day_date_view, this, false) as FrameLayout
    private var dayViewRow2: FrameLayout =
        layoutInflater.inflate(R.layout.layout_day_date_view, this, false) as FrameLayout
    private var dayViewRow3: FrameLayout =
        layoutInflater.inflate(R.layout.layout_day_date_view, this, false) as FrameLayout
    private var dayViewRow4: FrameLayout =
        layoutInflater.inflate(R.layout.layout_day_date_view, this, false) as FrameLayout
    private var dayViewRow5: FrameLayout =
        layoutInflater.inflate(R.layout.layout_day_date_view, this, false) as FrameLayout
    private var dayViewRow6: FrameLayout =
        layoutInflater.inflate(R.layout.layout_day_date_view, this, false) as FrameLayout
    private var monthTitle: TextView? = dayViewHeader.findViewById(R.id.txt_monthTitle) as TextView
    private var btn_next: ImageView? = dayViewHeader.findViewById(R.id.btn_next) as ImageView
    private var btn_previous: ImageView? = dayViewHeader.findViewById(R.id.btn_previous) as ImageView

    init {
        removeAllViews()
        orientation = LinearLayout.VERTICAL
        addView(dayViewHeader)
        addView(dayViewRow1)
        addView(dayViewRow2)
        addView(dayViewRow3)
        addView(dayViewRow4)
        addView(dayViewRow5)
        addView(dayViewRow6)

        monthTitle?.text = SimpleDateFormat(monthYearFormat, Locale.getDefault()).format(calender.time)
        btn_next?.setOnClickListener { nextMonth() }
        btn_previous?.setOnClickListener { previousMonth() }

        setOnTouchListener(object : OnCalendarSwipeListener(context) {
            override fun onSwipeLeft() {
                nextMonth()
            }

            override fun onSwipeRight() {
                previousMonth()
            }
        })
        updateEventView(true)
    }

    fun addEvent(eventItem: EventModal, clearOldData: Boolean = false) {

        if (clearOldData) eventList.clear()

        (dayViewRow1.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow2.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow3.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow4.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow5.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow6.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        eventList.add(eventItem)
        val sortedList = eventList.sortedWith(compareBy { it.getStartDateToSort() })
        eventList.clear()
        eventList.addAll(sortedList)
        updateEventView()
    }

    fun addEventList(eventListItems: ArrayList<EventModal>, clearOldData: Boolean = false) {
        if (clearOldData) eventList.clear()
        (dayViewRow1.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow2.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow3.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow4.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow5.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow6.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()

        val sortedList = eventListItems.sortedWith(compareBy { it.getStartDateToSort() })

        eventList.clear()
        eventList.addAll(sortedList)
        updateEventView()
    }

    private fun updateEventView(firstTime: Boolean = false) {
        var selectedCalender: Calendar = Calendar.getInstance()
        selectedCalender.set(calender.get(Calendar.YEAR), calender.get(Calendar.MONTH), calender.get(Calendar.DATE))
        selectedCalender.set(Calendar.DATE, 1)
        selectedCalender.set(Calendar.HOUR, 0); selectedCalender.set(
            Calendar.MINUTE,
            0
        ); selectedCalender.set(Calendar.SECOND, 0)

        calender.set(Calendar.DATE, 1)

        var totalDaysToShow: Int = calender.getActualMaximum(Calendar.DAY_OF_MONTH)

        val toFillInPreviousMonthDays = 1 - calender.get(Calendar.DAY_OF_WEEK)

        calender.set(Calendar.DATE, totalDaysToShow)

        val toFillInNextMonthDays = 7 - calender.get(Calendar.DAY_OF_WEEK)

        totalDaysToShow += Math.abs(toFillInPreviousMonthDays) + toFillInNextMonthDays

        calender.set(Calendar.DATE, 1)

        if (toFillInPreviousMonthDays != 0) {
            selectedCalender.add(Calendar.DAY_OF_YEAR, toFillInPreviousMonthDays)
        }

        val totalRows = totalDaysToShow.div(7)

        when (totalRows) {
            4 -> {
                dayViewRow5.visibility = View.GONE
                dayViewRow6.visibility = View.GONE
            }
            5 -> {
                dayViewRow5.visibility = View.VISIBLE
                dayViewRow6.visibility = View.GONE
            }
            else -> {
                dayViewRow5.visibility = View.VISIBLE
                dayViewRow6.visibility = View.VISIBLE
            }
        }

        val dateFormatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        (dayViewRow1.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow2.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow3.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow4.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow5.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()
        (dayViewRow6.findViewById(R.id.layout_tripEvents) as FlowLayout).removeAllViews()

        for (i in 1..totalDaysToShow step 7) {
            when (i) {
                in 1..7 -> {

                    val layout_tripEvents = dayViewRow1.findViewById(R.id.layout_tripEvents) as FlowLayout


                    val minDate = selectedCalender.time

                    val day1 = dayViewRow1.findViewById(R.id.day1) as TextView
                    day1.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    var widthOfText = day1.measuredWidth

                    val day2 = dayViewRow1.findViewById(R.id.day2) as TextView
                    day2.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day3 = dayViewRow1.findViewById(R.id.day3) as TextView
                    day3.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day4 = dayViewRow1.findViewById(R.id.day4) as TextView
                    day4.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day5 = dayViewRow1.findViewById(R.id.day5) as TextView
                    day5.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day6 = dayViewRow1.findViewById(R.id.day6) as TextView
                    day6.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val maxDate = selectedCalender.time

                    val day7 = dayViewRow1.findViewById(R.id.day7) as TextView
                    day7.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)


                    if (!firstTime) {
                        day1.postDelayed({
                            widthOfText = day1.measuredWidth
                            var eventAddCount = 0
                            for (j in 0 until eventList.size) {
                                val startDate = dateFormatter.parse(eventList.get(j).start)
                                val endDate = dateFormatter.parse(eventList.get(j).end)

                                if (eventAddCount == MAX_EVENT) {
                                    break
                                }

                                startDate.hours = 0; startDate.minutes = 0; startDate.seconds = 0
                                endDate.hours = 0; endDate.minutes = 0; endDate.seconds = 0

                                val eventTrip = layoutInflater.inflate(R.layout.layout_event_line, null) as CardView
                                val eventTitle = eventTrip.findViewById(R.id.txt_eventTitle) as TextView
                                eventTrip.setCardBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.setBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.text = eventList.get(j).title
                                eventTitle.setOnClickListener {
                                    eventClickListener?.onEventClick(eventList[j])
                                }

                                if (isDateInBetween(startDate, minDate, maxDate)) {

                                    val daysBetween =
                                        if (isSameDay(minDate, startDate)) 0 else getDaysBetween(minDate, startDate) + 1

                                    val startMarginDays = (widthOfText * daysBetween) + EXTRA_MARGIN
                                    var endMarginDays = 0

                                    if (isDateInBetween(endDate, minDate, maxDate)) {
                                        endMarginDays = (widthOfText * getDaysBetween(endDate, maxDate)) +
                                                EXTRA_MARGIN + widthOfText
                                    }

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)

                                    eventAddCount++

                                } else if (isDateInBetween(endDate, minDate, maxDate)) {
                                    val startMarginDays = 0
                                    val endMarginDays =
                                        (widthOfText * getDaysBetween(endDate, maxDate)) + EXTRA_MARGIN + widthOfText

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                } else if (isDateInBetween(minDate, startDate, maxDate) && isDateInBetween(
                                        maxDate,
                                        startDate,
                                        endDate
                                    )
                                ) {

                                    val startMarginDays = 0
                                    val endMarginDays = 0

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                }
                            }
                        }, POST_TIME)
                    }

                }
                in 8..14 -> {

                    val layout_tripEvents = dayViewRow2.findViewById(R.id.layout_tripEvents) as FlowLayout

                    var widthOfText = 0

                    val minDate = selectedCalender.time

                    val day1 = dayViewRow2.findViewById(R.id.day1) as TextView
                    day1.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)


                    val day2 = dayViewRow2.findViewById(R.id.day2) as TextView
                    day2.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day3 = dayViewRow2.findViewById(R.id.day3) as TextView
                    day3.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day4 = dayViewRow2.findViewById(R.id.day4) as TextView
                    day4.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day5 = dayViewRow2.findViewById(R.id.day5) as TextView
                    day5.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day6 = dayViewRow2.findViewById(R.id.day6) as TextView
                    day6.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val maxDate = selectedCalender.time

                    val day7 = dayViewRow2.findViewById(R.id.day7) as TextView
                    day7.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    if (!firstTime) {
                        day1.postDelayed({
                            widthOfText = day1.measuredWidth
                            var eventAddCount = 0
                            for (j in 0 until eventList.size) {
                                val startDate = dateFormatter.parse(eventList.get(j).start)
                                val endDate = dateFormatter.parse(eventList.get(j).end)

                                if (eventAddCount == MAX_EVENT) {
                                    break
                                }

                                startDate.hours = 0; startDate.minutes = 0; startDate.seconds = 0
                                endDate.hours = 0; endDate.minutes = 0; endDate.seconds = 0

                                val eventTrip = layoutInflater.inflate(R.layout.layout_event_line, null) as CardView
                                val eventTitle = eventTrip.findViewById(R.id.txt_eventTitle) as TextView
                                eventTrip.setCardBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.setBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.text = eventList.get(j).title
                                eventTitle.setOnClickListener { eventClickListener?.onEventClick(eventList[j]) }

                                if (isDateInBetween(startDate, minDate, maxDate)) {

                                    val daysBetween =
                                        if (isSameDay(minDate, startDate)) 0 else getDaysBetween(minDate, startDate) + 1

                                    val startMarginDays = (widthOfText * daysBetween) + EXTRA_MARGIN
                                    var endMarginDays = 0

                                    if (isDateInBetween(endDate, minDate, maxDate)) {
                                        endMarginDays = (widthOfText * getDaysBetween(endDate, maxDate)) +
                                                EXTRA_MARGIN + widthOfText
                                    }

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)

                                    eventAddCount++

                                } else if (isDateInBetween(endDate, minDate, maxDate)) {
                                    val startMarginDays = 0
                                    val endMarginDays =
                                        (widthOfText * getDaysBetween(endDate, maxDate)) + EXTRA_MARGIN + widthOfText

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                } else if (isDateInBetween(minDate, startDate, maxDate) && isDateInBetween(
                                        maxDate,
                                        startDate,
                                        endDate
                                    )
                                ) {

                                    val startMarginDays = 0
                                    val endMarginDays = 0

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                }
                            }
                        }, POST_TIME)
                    }

                }
                in 15..21 -> {

                    val layout_tripEvents = dayViewRow3.findViewById(R.id.layout_tripEvents) as FlowLayout

                    var widthOfText = 0
                    val minDate = selectedCalender.time
                    val day1 = dayViewRow3.findViewById(R.id.day1) as TextView
                    day1.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)


                    val day2 = dayViewRow3.findViewById(R.id.day2) as TextView
                    day2.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day3 = dayViewRow3.findViewById(R.id.day3) as TextView
                    day3.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day4 = dayViewRow3.findViewById(R.id.day4) as TextView
                    day4.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day5 = dayViewRow3.findViewById(R.id.day5) as TextView
                    day5.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day6 = dayViewRow3.findViewById(R.id.day6) as TextView
                    day6.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val maxDate = selectedCalender.time

                    val day7 = dayViewRow3.findViewById(R.id.day7) as TextView
                    day7.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    if (!firstTime) {
                        day1.postDelayed({
                            widthOfText = day1.measuredWidth
                            var eventAddCount = 0
                            for (j in 0 until eventList.size) {
                                val startDate = dateFormatter.parse(eventList.get(j).start)
                                val endDate = dateFormatter.parse(eventList.get(j).end)

                                if (eventAddCount == MAX_EVENT) {
                                    break
                                }

                                startDate.hours = 0; startDate.minutes = 0; startDate.seconds = 0
                                endDate.hours = 0; endDate.minutes = 0; endDate.seconds = 0

                                val eventTrip = layoutInflater.inflate(R.layout.layout_event_line, null) as CardView
                                val eventTitle = eventTrip.findViewById(R.id.txt_eventTitle) as TextView
                                eventTrip.setCardBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.setBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.text = eventList.get(j).title
                                eventTitle.setOnClickListener { eventClickListener?.onEventClick(eventList[j]) }

                                if (isDateInBetween(startDate, minDate, maxDate)) {

                                    val daysBetween =
                                        if (isSameDay(minDate, startDate)) 0 else getDaysBetween(minDate, startDate) + 1

                                    val startMarginDays = (widthOfText * daysBetween) + EXTRA_MARGIN
                                    var endMarginDays = 0

                                    if (isDateInBetween(endDate, minDate, maxDate)) {
                                        endMarginDays = (widthOfText * getDaysBetween(endDate, maxDate)) +
                                                EXTRA_MARGIN + widthOfText
                                    }

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)

                                    eventAddCount++

                                } else if (isDateInBetween(endDate, minDate, maxDate)) {
                                    val startMarginDays = 0
                                    val endMarginDays =
                                        (widthOfText * getDaysBetween(endDate, maxDate)) + EXTRA_MARGIN + widthOfText

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                } else if (isDateInBetween(minDate, startDate, maxDate) && isDateInBetween(
                                        maxDate,
                                        startDate,
                                        endDate
                                    )
                                ) {

                                    val startMarginDays = 0
                                    val endMarginDays = 0

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                }
                            }
                        }, POST_TIME)
                    }

                }
                in 22..28 -> {

                    val layout_tripEvents = dayViewRow4.findViewById(R.id.layout_tripEvents) as FlowLayout

                    var widthOfText = 0
                    val minDate = selectedCalender.time
                    val day1 = dayViewRow4.findViewById(R.id.day1) as TextView
                    day1.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)


                    val day2 = dayViewRow4.findViewById(R.id.day2) as TextView
                    day2.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day3 = dayViewRow4.findViewById(R.id.day3) as TextView
                    day3.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day4 = dayViewRow4.findViewById(R.id.day4) as TextView
                    day4.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day5 = dayViewRow4.findViewById(R.id.day5) as TextView
                    day5.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day6 = dayViewRow4.findViewById(R.id.day6) as TextView
                    day6.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val maxDate = selectedCalender.time
                    val day7 = dayViewRow4.findViewById(R.id.day7) as TextView
                    day7.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    if (!firstTime) {
                        day1.postDelayed({
                            widthOfText = day1.measuredWidth
                            var eventAddCount = 0
                            for (j in 0 until eventList.size) {
                                val startDate = dateFormatter.parse(eventList.get(j).start)
                                val endDate = dateFormatter.parse(eventList.get(j).end)

                                if (eventAddCount == MAX_EVENT) {
                                    break
                                }

                                startDate.hours = 0; startDate.minutes = 0; startDate.seconds = 0
                                endDate.hours = 0; endDate.minutes = 0; endDate.seconds = 0

                                val eventTrip = layoutInflater.inflate(R.layout.layout_event_line, null) as CardView
                                val eventTitle = eventTrip.findViewById(R.id.txt_eventTitle) as TextView
                                eventTrip.setCardBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.setBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.text = eventList.get(j).title
                                eventTitle.setOnClickListener { eventClickListener?.onEventClick(eventList[j]) }

                                if (isDateInBetween(startDate, minDate, maxDate)) {

                                    val daysBetween =
                                        if (isSameDay(minDate, startDate)) 0 else getDaysBetween(minDate, startDate) + 1

                                    val startMarginDays = (widthOfText * daysBetween) + EXTRA_MARGIN
                                    var endMarginDays = 0

                                    if (isDateInBetween(endDate, minDate, maxDate)) {
                                        endMarginDays = (widthOfText * getDaysBetween(endDate, maxDate)) +
                                                EXTRA_MARGIN + widthOfText
                                    }

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)

                                    eventAddCount++

                                } else if (isDateInBetween(endDate, minDate, maxDate)) {
                                    val startMarginDays = 0
                                    val endMarginDays =
                                        (widthOfText * getDaysBetween(endDate, maxDate)) + EXTRA_MARGIN + widthOfText

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                } else if (isDateInBetween(minDate, startDate, maxDate) && isDateInBetween(
                                        maxDate,
                                        startDate,
                                        endDate
                                    )
                                ) {

                                    val startMarginDays = 0
                                    val endMarginDays = 0

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                }
                            }
                        }, POST_TIME)
                    }
                }
                in 29..35 -> {

                    val layout_tripEvents = dayViewRow5.findViewById(R.id.layout_tripEvents) as FlowLayout

                    var widthOfText = 0

                    val minDate = selectedCalender.time
                    val day1 = dayViewRow5.findViewById(R.id.day1) as TextView
                    day1.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)


                    val day2 = dayViewRow5.findViewById(R.id.day2) as TextView
                    day2.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day3 = dayViewRow5.findViewById(R.id.day3) as TextView
                    day3.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day4 = dayViewRow5.findViewById(R.id.day4) as TextView
                    day4.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day5 = dayViewRow5.findViewById(R.id.day5) as TextView
                    day5.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day6 = dayViewRow5.findViewById(R.id.day6) as TextView
                    day6.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val maxDate = selectedCalender.time
                    val day7 = dayViewRow5.findViewById(R.id.day7) as TextView
                    day7.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    if (!firstTime) {
                        day1.postDelayed({
                            widthOfText = day1.measuredWidth
                            var eventAddCount = 0
                            for (j in 0 until eventList.size) {
                                val startDate = dateFormatter.parse(eventList.get(j).start)
                                val endDate = dateFormatter.parse(eventList.get(j).end)

                                if (eventAddCount == MAX_EVENT) {
                                    break
                                }

                                startDate.hours = 0; startDate.minutes = 0; startDate.seconds = 0
                                endDate.hours = 0; endDate.minutes = 0; endDate.seconds = 0

                                val eventTrip = layoutInflater.inflate(R.layout.layout_event_line, null) as CardView
                                val eventTitle = eventTrip.findViewById(R.id.txt_eventTitle) as TextView
                                eventTrip.setCardBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.setBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.text = eventList.get(j).title
                                eventTitle.setOnClickListener { eventClickListener?.onEventClick(eventList[j]) }

                                if (isDateInBetween(startDate, minDate, maxDate)) {

                                    val daysBetween =
                                        if (isSameDay(minDate, startDate)) 0 else getDaysBetween(minDate, startDate) + 1

                                    val startMarginDays = (widthOfText * daysBetween) + EXTRA_MARGIN
                                    var endMarginDays = 0

                                    if (isDateInBetween(endDate, minDate, maxDate)) {
                                        endMarginDays = (widthOfText * getDaysBetween(endDate, maxDate)) +
                                                EXTRA_MARGIN + widthOfText
                                    }

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)

                                    eventAddCount++

                                } else if (isDateInBetween(endDate, minDate, maxDate)) {
                                    val startMarginDays = 0
                                    val endMarginDays =
                                        (widthOfText * getDaysBetween(endDate, maxDate)) + EXTRA_MARGIN + widthOfText

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                } else if (isDateInBetween(minDate, startDate, maxDate) && isDateInBetween(
                                        maxDate,
                                        startDate,
                                        endDate
                                    )
                                ) {

                                    val startMarginDays = 0
                                    val endMarginDays = 0

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                }
                            }
                        }, POST_TIME)
                    }
                }
                in 36..42 -> {

                    val layout_tripEvents = dayViewRow6.findViewById(R.id.layout_tripEvents) as FlowLayout

                    var widthOfText = 0

                    val minDate = selectedCalender.time
                    val day1 = dayViewRow6.findViewById(R.id.day1) as TextView
                    day1.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)


                    val day2 = dayViewRow6.findViewById(R.id.day2) as TextView
                    day2.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day3 = dayViewRow6.findViewById(R.id.day3) as TextView
                    day3.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day4 = dayViewRow6.findViewById(R.id.day4) as TextView
                    day4.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day5 = dayViewRow6.findViewById(R.id.day5) as TextView
                    day5.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val day6 = dayViewRow6.findViewById(R.id.day6) as TextView
                    day6.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    val maxDate = selectedCalender.time
                    val day7 = dayViewRow6.findViewById(R.id.day7) as TextView
                    day7.text = selectedCalender.get(Calendar.DATE).toString()
                    selectedCalender = addSingleDay(selectedCalender)

                    if (!firstTime) {
                        day1.postDelayed({
                            widthOfText = day1.measuredWidth
                            var eventAddCount = 0
                            for (j in 0 until eventList.size) {
                                val startDate = dateFormatter.parse(eventList.get(j).start)
                                val endDate = dateFormatter.parse(eventList.get(j).end)

                                if (eventAddCount == MAX_EVENT) {
                                    break
                                }

                                startDate.hours = 0; startDate.minutes = 0; startDate.seconds = 0
                                endDate.hours = 0; endDate.minutes = 0; endDate.seconds = 0

                                val eventTrip = layoutInflater.inflate(R.layout.layout_event_line, null) as CardView
                                val eventTitle = eventTrip.findViewById(R.id.txt_eventTitle) as TextView
                                eventTrip.setCardBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.setBackgroundColor(Color.parseColor(eventList.get(j).color))
                                eventTitle.text = eventList.get(j).title
                                eventTitle.setOnClickListener { eventClickListener?.onEventClick(eventList[j]) }

                                if (isDateInBetween(startDate, minDate, maxDate)) {

                                    val daysBetween =
                                        if (isSameDay(minDate, startDate)) 0 else getDaysBetween(minDate, startDate) + 1

                                    val startMarginDays = (widthOfText * daysBetween) + EXTRA_MARGIN
                                    var endMarginDays = 0

                                    if (isDateInBetween(endDate, minDate, maxDate)) {
                                        endMarginDays = (widthOfText * getDaysBetween(endDate, maxDate)) +
                                                EXTRA_MARGIN + widthOfText
                                    }

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)

                                    eventAddCount++

                                } else if (isDateInBetween(endDate, minDate, maxDate)) {
                                    val startMarginDays = 0
                                    val endMarginDays =
                                        (widthOfText * getDaysBetween(endDate, maxDate)) + EXTRA_MARGIN + widthOfText

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                } else if (isDateInBetween(minDate, startDate, maxDate) && isDateInBetween(
                                        maxDate,
                                        startDate,
                                        endDate
                                    )
                                ) {

                                    val startMarginDays = 0
                                    val endMarginDays = 0

                                    val params =
                                        LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT)
                                    params.setMargins(startMarginDays, 5, endMarginDays, 5)
                                    layout_tripEvents.addView(eventTrip, params)
                                    eventAddCount++
                                }
                            }
                        }, POST_TIME)
                    }
                }
            }
        }
    }

    private fun setMonthTextColor(color: Int) {
        (dayViewHeader.findViewById(R.id.txt_monthTitle) as TextView).setTextColor(color)
    }

    private fun setWeekDayTitleTextColor(color: Int) {
        (dayViewHeader.findViewById(R.id.txt_sun) as TextView).setTextColor(color)
        (dayViewHeader.findViewById(R.id.txt_mon) as TextView).setTextColor(color)
        (dayViewHeader.findViewById(R.id.txt_tue) as TextView).setTextColor(color)
        (dayViewHeader.findViewById(R.id.txt_wed) as TextView).setTextColor(color)
        (dayViewHeader.findViewById(R.id.txt_thu) as TextView).setTextColor(color)
        (dayViewHeader.findViewById(R.id.txt_fri) as TextView).setTextColor(color)
        (dayViewHeader.findViewById(R.id.txt_sat) as TextView).setTextColor(color)
    }

    fun setHeaderColor(color: Int) {
        setMonthTextColor(color)
        setWeekDayTitleTextColor(color)
    }

    private fun addSingleDay(calendar: Calendar): Calendar {
        val currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH)
        val currentDayOfYear = calendar.get(Calendar.DAY_OF_YEAR)

        val copyCalender: Calendar = calendar.clone() as Calendar
        copyCalender.set(Calendar.DATE, 1)

        val maxDaysInMonth = copyCalender.getMaximum(Calendar.DAY_OF_MONTH)

        if (currentDayOfMonth == maxDaysInMonth) {
            if (currentDayOfYear == calendar.getActualMaximum(Calendar.DAY_OF_YEAR)) {
                calendar.add(Calendar.YEAR, 1)
                calendar.set(Calendar.MONTH, Calendar.JANUARY)
                calendar.set(Calendar.DAY_OF_YEAR, 1)
            } else {
                calendar.add(Calendar.MONTH, 1)
                calendar.set(Calendar.DATE, 1)
            }
        } else {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        return calendar
    }

    fun nextMonth() {
        calender.add(Calendar.MONTH, 1)
        monthTitle?.text = SimpleDateFormat(monthYearFormat, Locale.getDefault()).format(calender.time)
        updateEventView()
    }

    fun previousMonth() {
        calender.add(Calendar.MONTH, -1)
        monthTitle?.text = SimpleDateFormat(monthYearFormat, Locale.getDefault()).format(calender.time)
        updateEventView()
    }

    private fun isDateInBetween(date: Date, minDate: Date, maxDate: Date): Boolean {
        date.hours = 0; date.minutes = 0; date.seconds = 0
        minDate.hours = 0; minDate.minutes = 0; minDate.seconds = 0
        maxDate.hours = 0; maxDate.minutes = 0; maxDate.seconds = 0
        if (isSameDay(date, minDate) || isSameDay(date, maxDate)) {
            return true
        }
        if (date.compareTo(minDate) >= 0 && date.compareTo(maxDate) <= 0) {
            return true
        }
        return false
    }

    private fun isSameDay(date1: Date, date2: Date): Boolean {
        val calender1 = Calendar.getInstance()
        val calender2 = Calendar.getInstance()
        calender1.time = date1; calender2.time = date2
        return calender1.get(Calendar.YEAR) == calender2.get(Calendar.YEAR) && calender1.get(Calendar.DAY_OF_YEAR) == calender2.get(
            Calendar.DAY_OF_YEAR
        )
    }

    private fun getDaysBetween(startDate: Date, endDate: Date): Int {
        startDate.hours = 0; startDate.minutes = 0; startDate.seconds = 0
        endDate.hours = 0; endDate.minutes = 0; endDate.seconds = 0

        val diff = endDate.time - startDate.time

        return TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS).toInt()
    }

    fun setCalenderEventClickListener(eventClickListener: CalenderEventClickListener) {
        this.eventClickListener = eventClickListener
    }

    fun setMaxEventToShowPerWeek(maxEventCount: Int) {
        this.MAX_EVENT = maxEventCount
        updateEventView()
    }

    interface CalenderEventClickListener {
        fun onEventClick(eventItem: EventModal)
    }

}