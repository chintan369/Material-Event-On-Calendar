# Material-Event-On-Calendar
This is the unique library to show events on calendar with different colors and event names with startign date and end date

[![](https://jitpack.io/v/chintan369/Material-Event-On-Calendar.svg)](https://jitpack.io/#chintan369/Material-Event-On-Calendar)

### Add it in your root build.gradle at the end of repositories:

```
allprojects {
 repositories {
  ...
	maven { url 'https://jitpack.io' }
	}
 }
```

### Step 2. Add the dependency

```
dependencies {
	implementation 'com.github.chintan369:Material-Event-On-Calendar:latest-release'
}
``` 

and lastly put it in your layout.xml file as

```
<com.chintanpatel.materialeventcalendar.CalenderView
            android:id="@+id/eventCalendar"
            android:layout_width="match_parent"
            android:layout_height="match_parent"/>
```

and in your class/ kotlin file, you can use to add event as below.

```
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
 
```
In **addEventList() / addEvent()**, if you will pass **true** as second argumnet, it will clear previous events and add only you passed in first argument.

You can also change the header text color using **setHeaderColor(color)**
