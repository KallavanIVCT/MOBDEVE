package com.example.mobdeve

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdeve.databinding.MainMenuBinding
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Locale

class MainTodoActivity : AppCompatActivity(), checkListenerCb {
    private lateinit var viewBinding: MainMenuBinding
    private var userId: Long = -1;
    private lateinit var username: String;
    private lateinit var listTodo: ArrayList<TodoModel>
    private lateinit var modifiedListTodo: ArrayList<TodoModel>
    private lateinit var dbHelper: DBHelper
    private lateinit var adapter: MyAdapter
    private lateinit var sortChoice: String
    private var currentEdit : String = ""

    private fun todoArrayTimeConvert(listTodo: ArrayList<TodoModel>) : Unit{
        for (todo in listTodo){
            val oldDueDate = todo.dueDate;
            val oldAlarmDate = todo.AlarmDate;
            // formatting
            val originalFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            val newTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format for AM/PM
            // making date to actual date type
            val dateDue = originalFormatter.parse(oldDueDate)
            val dateAlarm = originalFormatter.parse(oldAlarmDate)
            // extract the date
            val dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateDue) // keep date
            val dueTime = newTimeFormatter.format(dateDue) // format
            val alarmDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateAlarm) // keep date
            val alarmTime = newTimeFormatter.format(dateAlarm) // format
            // Combine them back into the desired format
            val newDueDate = "$dueDate $dueTime"
            val newAlarmDate = "$alarmDate $alarmTime"
            todo.dueDate = newDueDate
            todo.AlarmDate = newAlarmDate
        }
    }

    private fun todoSpecificTimeConvert(todo: TodoModel){
        val oldDueDate = todo.dueDate;
        val oldAlarmDate = todo.AlarmDate;

        // format
        val originalFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
        val newTimeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format for AM/PM

        // make date to type date
        val dateDue = originalFormatter.parse(oldDueDate)
        val dateAlarm = originalFormatter.parse(oldAlarmDate)

        // extract and format time and date
        val dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateDue) // keep date
        val dueTime = newTimeFormatter.format(dateDue) // format time
        val alarmDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(dateAlarm) // Keep date
        val alarmTime = newTimeFormatter.format(dateAlarm) // format time
        // combine them again
        val newDueDate = "$dueDate $dueTime"
        val newAlarmDate = "$alarmDate $alarmTime"
        todo.dueDate = newDueDate
        todo.AlarmDate = newAlarmDate
    }
    override fun onCreate(savedInstanceState: Bundle?) {

        userId = getUserId()

        dbHelper = DBHelper(this, null)
        listTodo = ArrayList(dbHelper.getTodo(userId = userId))


        todoArrayTimeConvert(listTodo)


        modifiedListTodo = listTodo

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()



        viewBinding = MainMenuBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        setupNotificationChannel()

        val myActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == RESULT_OK) {
                val intent = result.data
                val option: String = intent?.getStringExtra("inputOptions").toString()


                when (option){
                    "add" -> {
                        viewBinding.edtName.setText("")
                        val title: String = intent?.getStringExtra("inputTitle").toString()
                        Log.d("LOG", title)
                        val addedTodo: TodoModel? = dbHelper.getSpecificTodo(title, userId)
                        Log.d("LOG", addedTodo.toString())
                        if (addedTodo != null) {
                            todoSpecificTimeConvert(addedTodo)
                            listTodo.add(addedTodo)
                            modifiedListTodo = listTodo
                            viewBinding.rvRecycler.adapter?.notifyItemInserted(listTodo.size - 1)
                        }else{
                            Log.d("LOG","failed viewing just added todo - failed view")
                        }
                    }
                    "update" -> {
                        viewBinding.edtName.setText("")
                        val title: String = intent?.getStringExtra("inputTitle").toString()
                        val position: Int? = intent?.getIntExtra("inputPosition", -1)
                        Log.d("LOGP", position.toString())
                        if (position != null) {
                            val updatedTodo = dbHelper.getSpecificTodo(title, userId)!!
                            todoSpecificTimeConvert(updatedTodo)
                            listTodo[position] =  updatedTodo
                            modifiedListTodo = listTodo
                            viewBinding.rvRecycler.adapter?.notifyItemChanged(position)
                        }

                    }
                }


            } else if (result.resultCode == RESULT_CANCELED) {

            }
        }
        adapter = MyAdapter(listTodo, myActivityLauncher, this, dbHelper)
        viewBinding.rvRecycler.adapter = adapter
        viewBinding.rvRecycler.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)


        username = intent.getStringExtra("user_username").toString()

        // DROPDOWN
        val dropdown: Spinner = viewBinding.imgvOptions
        val items: ArrayList<String> = arrayListOf("Subject Type", "Difficulty", "Date", "unfinished")
        val dropdownadapter: ArrayAdapter<String> = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, items)
        dropdown.adapter = dropdownadapter
        //https://www.youtube.com/watch?v=4ogzfAipGS8

        // helpers for sorting
        val sortedValues = mapOf("Easy" to 1, "Medium" to 2  ,"Hard" to 3 )
        val dateFormat = SimpleDateFormat("yyyy-MM-dd hh:mm a", Locale.getDefault())

        dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {

                sortChoice = items[position]
                Log.d("SORTING", sortChoice);
                if (items[position] == "Subject Type") {
                    listTodo.sortBy {it.type}
                }
                if (items[position] == "Difficulty") {
                    listTodo.sortBy {sortedValues[it.difficulty]}
                }
                if (items[position] == "Date") {
                    listTodo.sortBy { dateFormat.parse(it.dueDate) }
                }
                if (items[position] == "Unfinished") {
                    listTodo.sortBy {it.isFinished}
                }


                if (items[position] == "Subject Type") {
                    modifiedListTodo.sortBy {it.type}
                }
                if (items[position] == "Difficulty") {
                    modifiedListTodo.sortBy {sortedValues[it.difficulty]}
                }
                if (items[position] == "Date") {
                    modifiedListTodo.sortBy { dateFormat.parse(it.dueDate) }
                }
                if (items[position] == "Unfinished") {
                    modifiedListTodo.sortBy {it.isFinished}
                }
                viewBinding.rvRecycler.adapter?.notifyDataSetChanged()
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }


        viewBinding.edtName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentEdit = s.toString();
                if (s.isNullOrEmpty()){
                    adapter = MyAdapter(listTodo, myActivityLauncher, this@MainTodoActivity, dbHelper)
                    viewBinding.rvRecycler.adapter = adapter
                }else{
                    modifiedListTodo = listTodo

                    val query = s.toString()
                    Log.d("ZZ", query)
                    //var matchingList: ArrayList<TodoModel> = ArrayList(listTodo.filter{ it.title.contains(query, ignoreCase = true)})
                    //var nonMatchingList = ArrayList(listTodo.filterNot{ it.title.contains(query, ignoreCase = true)})
                    Log.d("ZZ", modifiedListTodo.toString() + "dsadsa")
                    modifiedListTodo = ArrayList(modifiedListTodo.filter {it.title.contains(query, ignoreCase = true)})
                    Log.d("ZZ", modifiedListTodo.toString() + "dsadsa")
                    Log.d("sort", sortChoice)
                    if (sortChoice.isNullOrEmpty()){
                        sortChoice = "Subject Type";
                    }
                    Log.d("sort", sortChoice + "final")
                    if (sortChoice == "Subject Type") {
                        modifiedListTodo.sortBy {it.type}
                    }
                    if (sortChoice == "Difficulty") {
                        modifiedListTodo.sortBy {sortedValues[it.difficulty]}
                    }
                    if (sortChoice == "Date") {
                        modifiedListTodo.sortBy { dateFormat.parse(it.dueDate) }
                    }
                    if (sortChoice == "Unfinished") {
                        modifiedListTodo.sortBy {it.isFinished}
                    }
                    adapter = MyAdapter(modifiedListTodo, myActivityLauncher, this@MainTodoActivity, dbHelper)
                    viewBinding.rvRecycler.adapter = adapter
                    adapter.notifyDataSetChanged()
                    }
                }


            override fun afterTextChanged(s: Editable?) {}
        })



        // This is used to create like seperation between the rows of recycler view
        viewBinding.rvRecycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                outRect.bottom = 50
            }
        })


        viewBinding.imgvAdd.setOnClickListener(){
            val intent = Intent(this, AddTodoActivity::class.java)
            myActivityLauncher.launch(intent)

        }



    }
    private fun A(){}

    private fun getUserId(): Long {
        val sharedPreferences = getSharedPreferences("mainPreference", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getLong("USER_ID", -1)
        return userId
    }
    private fun setupNotificationChannel() {
        // thisis cause api 26 above dapat
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Todo Notifications"
            val descriptionText = "Notifications for todo reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel("TODO_CHANNEL", name, importance).apply {
                description = descriptionText
            }

            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onTodoCheckedChange(todo: TodoModel, isChecked: Int) {
        todo.isFinished = (isChecked == 1)
        dbHelper.updateTodoStatus(todo.title, isChecked) // update the database
    }
}