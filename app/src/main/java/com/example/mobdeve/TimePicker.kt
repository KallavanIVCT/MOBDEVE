package com.example.mobdeve

import android.app.Dialog
import androidx.fragment.app.DialogFragment
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.format.DateFormat
import android.widget.TimePicker
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


interface OnTimePass {
    fun onTimePass(data: String)
}
class TimePickerFragment : DialogFragment(), TimePickerDialog.OnTimeSetListener {

    // this is basically ung main activity context
    lateinit var dataPasser: OnTimePass
    //context here is the main activity context or whatever the fragment attaches to
    override fun onAttach(context: Context) {
        //meaning fragment attaches to main activity
        super.onAttach(context)
        // it can work with just context but adding as OnDataPass means it checks if it implements the abstract interface
        dataPasser = context as OnTimePass
    }
    //basically making the timepicker dialog and showing it
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //default values for the picker.
        val c = Calendar.getInstance()
        val hour = c.get(Calendar.HOUR_OF_DAY)
        val minute = c.get(Calendar.MINUTE)


        return TimePickerDialog(activity, this, hour, minute, DateFormat.is24HourFormat(activity))
    }
    // what happens after the user press ok
    override fun onTimeSet(view: TimePicker, hourOfDay: Int, minute: Int) {

        val calendar = Calendar.getInstance()

        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)


        val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val time = timeFormat.format(calendar.time)



        passData(time)
    }
    fun passData(data: String){
        dataPasser.onTimePass(data)
    }
}