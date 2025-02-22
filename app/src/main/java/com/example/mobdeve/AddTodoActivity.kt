package com.example.mobdeve

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mobdeve.databinding.AddTodoMenuBinding
import java.time.format.DateTimeFormatter

// OnDatePass interface para interact with fragment to activity
class AddTodoActivity : AppCompatActivity(), OnDatePass, OnTimePass {
    private var userId: Long = -1;
    private  var dueDate: String = "";
    private  var dueTime: String = "";
    private  var alarmTime: String = "";
    private  var alarmDate: String = "";
    private lateinit var viewBinding: AddTodoMenuBinding;
    private lateinit var dbHelper: DBHelper

    // to Seperate ung duetime and alarmtime since same sila nang ginagamit na class
    private var isDueTime: Boolean = false
    private var isDueDate: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        userId = getUserId()
        super.onCreate(savedInstanceState)
        viewBinding = AddTodoMenuBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        dbHelper = DBHelper(this,null)

        viewBinding.btnAddDueTime.setOnClickListener(){
            isDueTime = true
            val timePicker: TimePickerFragment = TimePickerFragment()
            timePicker.show(supportFragmentManager, "timepicker")
        }
        viewBinding.btnAddDueDate.setOnClickListener(){
            isDueDate = true
            val datePicker: DatePickerFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "datepicker")
        }
        viewBinding.btnAddAlarmTime.setOnClickListener(){
            val timePicker: TimePickerFragment = TimePickerFragment()
            timePicker.show(supportFragmentManager, "timepicker")
        }
        viewBinding.btnAddAlarmDate.setOnClickListener(){
            val datePicker: DatePickerFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "datepicker")
        }

        viewBinding.btnAddTodo.setOnClickListener(){
            validateAddUserInput()
        }
        viewBinding.btnAddCancel.setOnClickListener(){
            cancelAddUserInput()
        }

    }

    override fun onTimePass(data: String) {
        if(isDueTime){
            Log.d("LOG", "DueTime: " + data)
            dueTime = data
            viewBinding.btnAddDueTime.text = dueTime
            isDueTime = false
        }else{
            Log.d("LOG", "AlarmTime: " + data)
            alarmTime = data
            viewBinding.btnAddAlarmTime.text = alarmTime
        }

    }
    override fun onDatePass(data: String) {
        if(isDueDate){
            Log.d("LOG", "DueDate: " + data)
            dueDate = data
            viewBinding.btnAddDueDate.text = dueDate
            isDueDate = false
        }else{
            Log.d("LOG", "AlarmDate: " + data)
            alarmDate = data
            viewBinding.btnAddAlarmDate.text = alarmDate
        }
    }
    fun cancelAddUserInput(){
        setResult(RESULT_CANCELED, intent)
        finish()
    }
    fun validateAddUserInput(){
        var title : String = viewBinding.etAddTitle.text.toString()
        var type: String = viewBinding.etAddSubject.text.toString()
        var description: String = viewBinding.etAddDescription.text.toString()
        var subject: String = viewBinding.etAddSubject.text.toString()
        var subjectType: String = ""
        var difficulty: String = ""
        if(viewBinding.rbAddGE.isChecked){
            subjectType = viewBinding.rbAddGE.text.toString()
        }
        if(viewBinding.rbAddLC.isChecked){
            subjectType = viewBinding.rbAddLC.text.toString()
        }
        if(viewBinding.rbAddMajor.isChecked){
            subjectType = viewBinding.rbAddMajor.text.toString()
        }
        if(viewBinding.rbAddEasy.isChecked){
            difficulty = viewBinding.rbAddEasy.text.toString()
        }
        if(viewBinding.rbAddMedium.isChecked){
            difficulty = viewBinding.rbAddMedium.text.toString()
        }
        if(viewBinding.rbAddHard.isChecked){
            difficulty = viewBinding.rbAddHard.text.toString()
        }





        if(title.isNotEmpty() && type.isNotEmpty() && description.isNotEmpty() && subject.isNotEmpty() && subjectType.isNotEmpty() && difficulty.isNotEmpty() && dueTime.isNotEmpty() && dueDate.isNotEmpty() && alarmTime.isNotEmpty() && alarmDate.isNotEmpty()){
            var dueCombinedDate: String =  "${dueDate} ${dueTime}.000";
            var alarmCombinedDate: String = "${alarmDate} ${alarmTime}.000";
            Log.d("LOG", "title in addtodo function: " + title)
            // this is where to put the alarm notif make alarm ther function is in DBHELPER
            val result: Long = dbHelper.createTodo(userId, title, subject, subjectType, description, difficulty, alarmCombinedDate, dueCombinedDate, isDone=1 )
            if (result > 0){
                Log.d("LOG", "SUCCESFUL ADDING IN DATABASE")
                dbHelper.scheduleTodoNotification(this, result.toInt(), title,  alarmCombinedDate)
            }else{
                Log.d("LOG", "FAILED ADDING IN DATABASE")
            }
            // passsing title cause main activity would query a model based on the title passed on intent
            val intent = intent.putExtra("inputTitle", title)
            intent.putExtra("inputOptions", "add") // for distinguishing if update or add or delete
            setResult(Activity.RESULT_OK, intent)
            finish()
        }else{
            Toast.makeText(this, "Please fill out the required fields", Toast.LENGTH_SHORT).show()
        }

    }
    private fun getUserId(): Long {
        val sharedPreferences = getSharedPreferences("mainPreference", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("USER_ID", -1)
        return userId
    }


}

