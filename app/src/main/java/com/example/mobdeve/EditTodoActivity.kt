package com.example.mobdeve

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mobdeve.databinding.AddTodoMenuBinding
import com.example.mobdeve.databinding.EditTodoMenuBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditTodoActivity : AppCompatActivity(), OnTimePass, OnDatePass {
    private var dueDate: String = "";
    private  var dueTime: String = "";
    private  var alarmTime: String = "";
    private  var alarmDate: String = "";
    private lateinit var viewBinding: EditTodoMenuBinding;
    private lateinit var dbHelper: DBHelper
    private var position: Int = -1 // default value lang
    private lateinit var originalTitle: String
    private var id: Long = -1

    // to Seperate ung duetime and alarmtime since same sila nang ginagamit na class
    private var isDueTime: Boolean = false
    private var isDueDate: Boolean = false

    private fun AMPMToTime(time: String): String {
        // time is like "9:00 AM" -> Convert to "09:00:00"

        // define the formatter for input time
        val originalTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault()) // format for AM/PM
        // define the formatter for output time
        val newTimeFormatter = SimpleDateFormat("HH:mm:ss", Locale.getDefault()) // 24-hour format

        val date: Date? = originalTimeFormatter.parse(time)

        if (date == null) {
            return ""
        }

        // format the parsed Date to the new format
        return newTimeFormatter.format(date)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = EditTodoMenuBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        dbHelper = DBHelper(this,null)





        id = intent.getLongExtra("inputIDTodo", -1)
        //cause this would be used to know what tod/o is being edited since user might edit the title
        originalTitle = intent.getStringExtra("inputTitle").toString()
        // selected position of the one selected -> used for main activity to notify adapter
        position = intent.getIntExtra("inputPosition", -1)
        // THIS CODE TO put the preexisting data in the forms already
        viewBinding.etEditTitle.setText(intent.getStringExtra("inputTitle").toString())
        viewBinding.etEditDescription.setText(intent.getStringExtra("inputDescription").toString())
        viewBinding.etEditSubject.setText(intent.getStringExtra("inputSubject").toString())

        //date time
        val dateDueString = intent.getStringExtra("inputDueDate").toString()
        val formattedDueTime = AMPMToTime(dateDueString.substring(11))
        val formattedDueDate = dateDueString.substring(0,10)
        dueDate = formattedDueDate
        dueTime = formattedDueTime
        viewBinding.btnEditDueDate.setText(formattedDueDate)
        viewBinding.btnEditDueTime.setText(formattedDueTime)




        val dateAlarmString = intent.getStringExtra("inputAlarmDate").toString()
        val formattedAlarmTime = AMPMToTime(dateAlarmString.substring(11))
        val formattedAlarmDate = dateAlarmString.substring(0,10)
        alarmDate = formattedAlarmDate
        alarmTime = formattedAlarmTime
        viewBinding.btnEditAlarmTime.setText(formattedAlarmTime)
        viewBinding.btnEditAlarmDate.setText(formattedAlarmDate)


        val subjectType: String = intent.getStringExtra("inputSubjectType").toString()
        Log.d("LOG", "ST" + subjectType)
        val difficulty: String = intent.getStringExtra("inputDifficulty").toString()

        if (subjectType == "GE"){
            viewBinding.rbEditSubjectType.check(viewBinding.rbEditGE.id)
        }
        if (subjectType == "LC"){
            viewBinding.rbEditSubjectType.check(viewBinding.rbEditLC.id)
        }
        if (subjectType == "Major"){
            viewBinding.rbEditSubjectType.check(viewBinding.rbEditMajor.id)
        }
        if (difficulty == "Easy"){
             viewBinding.rbEditDifficulty.check(viewBinding.rbEditEasy.id)
            }
         if (difficulty == "Medium"){
             viewBinding.rbEditDifficulty.check(viewBinding.rbEditMedium.id)
         }
         if (difficulty == "Hard"){
             viewBinding.rbEditDifficulty.check(viewBinding.rbEditHard.id)
         }



        viewBinding.btnEditDueTime.setOnClickListener(){
            isDueTime = true
            val timePicker: TimePickerFragment = TimePickerFragment()
            timePicker.show(supportFragmentManager, "timepicker")
        }
        viewBinding.btnEditDueDate.setOnClickListener(){
            isDueDate = true
            val datePicker: DatePickerFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "datepicker")
        }
        viewBinding.btnEditAlarmTime.setOnClickListener(){
            val timePicker: TimePickerFragment = TimePickerFragment()
            timePicker.show(supportFragmentManager, "timepicker")
        }
        viewBinding.btnEditAlarmDate.setOnClickListener(){
            val datePicker: DatePickerFragment = DatePickerFragment()
            datePicker.show(supportFragmentManager, "datepicker")
        }

        viewBinding.btnEditTodo.setOnClickListener(){
            validateEditUserInput()
        }
        viewBinding.btnEditCancel.setOnClickListener(){
            cancelEditUserInput()
        }

    }

    override fun onTimePass(data: String) {
        if(isDueTime){
            Log.d("LOG", "DueTime: " + data)
            dueTime = data
            viewBinding.btnEditDueTime.text = dueTime
            isDueTime = false
        }else{
            Log.d("LOG", "AlarmTime: " + data)
            alarmTime = data
            viewBinding.btnEditAlarmTime.text = alarmTime
        }

    }
    override fun onDatePass(data: String) {
        if(isDueDate){
            Log.d("LOG", "DueDate: " + data)
            dueDate = data
            viewBinding.btnEditDueDate.text = dueDate
            isDueDate = false
        }else{
            Log.d("LOG", "AlarmDate: " + data)
            alarmDate = data
            viewBinding.btnEditAlarmDate.text = alarmDate
        }
    }
    fun cancelEditUserInput(){
        setResult(RESULT_CANCELED, intent)
        finish()
    }
    fun validateEditUserInput(){
        var title : String = viewBinding.etEditTitle.text.toString()
        var type: String = viewBinding.etEditSubject.text.toString()
        var description: String = viewBinding.etEditDescription.text.toString()
        var subject: String = viewBinding.etEditSubject.text.toString()
        var subjectType: String = ""
        var difficulty: String = ""
        if(viewBinding.rbEditGE.isChecked){
            subjectType = viewBinding.rbEditGE.text.toString()
        }
        if(viewBinding.rbEditLC.isChecked){
            subjectType = viewBinding.rbEditLC.text.toString()
        }
        if(viewBinding.rbEditMajor.isChecked){
            subjectType = viewBinding.rbEditMajor.text.toString()
        }
        if(viewBinding.rbEditEasy.isChecked){
            difficulty = viewBinding.rbEditEasy.text.toString()
        }
        if(viewBinding.rbEditMedium.isChecked){
            difficulty = viewBinding.rbEditMedium.text.toString()
        }
        if(viewBinding.rbEditHard.isChecked){
            difficulty = viewBinding.rbEditHard.text.toString()
        }




        // fix this next time add due time date and alarm time date
        if(title.isNotEmpty() && type.isNotEmpty() && description.isNotEmpty() && subject.isNotEmpty() && subjectType.isNotEmpty() && difficulty.isNotEmpty() && dueTime.isNotEmpty() && dueDate.isNotEmpty() && alarmTime.isNotEmpty() && alarmDate.isNotEmpty()){
            var dueCombinedDate: String =  "$dueDate $dueTime.000";
            var alarmCombinedDate: String = "$alarmDate $alarmTime.000"
            Log.d("LOG", "title in edittodo function: " + title)
            val isDone: Int = if (intent.getBooleanExtra("inputIsDone", false)) 1 else 0
            // // this is where to put the alarm notif makealarm. there function is in DBHELPER
            Log.d("GR", isDone.toString())
            val result: Boolean = dbHelper.updateTodo(originalTitle, title, subject, subjectType, description, difficulty, alarmCombinedDate, dueCombinedDate, isDone)
            if (result){
                Log.d("LOG", "SUCCESFUL EDIT IN DATABASE")
                dbHelper.scheduleTodoNotification(this, id.toInt(), title, alarmCombinedDate)
            }else{
                Log.d("LOG", "FAILED EDIT IN DATABASE")
            }
            // passing title cause main activity would query a model based on the title passed on intent
            val intent = intent.putExtra("inputTitle", title)
            intent.putExtra("inputOptions", "update")
            intent.putExtra("inputPosition", position)
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