package com.example.mobdeve

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DBHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION) {

    companion object{

        private val DATABASE_NAME = "MOBDEVE"


        private val DATABASE_VERSION = 1

        val TABLE_USER = "user"
        val USERNAME_COL = "username"
        val PASSWORD_COL = "password"

        val TABLE_TODO = "todo"
        val USER_ID = "user_id"
        val TITLE_COL = "title"
        val SUBJECT_COL = "subject"
        val SUBJECT_TYPE_COL = "subject_type"
        val DESCRIPTION_COL = "description"
        val DIFFICULTY_COL = "difficulty"
        val ALARM_DATE_COL = "alarm_date"
        val DUE_DATE_COL = "due_date"
        val IS_DONE_COL = "is_done"
    }


    override fun onCreate(db: SQLiteDatabase) {

        val tableCreateUser = (
                "CREATE TABLE " + TABLE_USER + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                USERNAME_COL + " TEXT, " +
                PASSWORD_COL + " TEXT, " +
                "UNIQUE (" + USERNAME_COL + ") " +
                ")"
                )
        val tableCreateTodo = """
                CREATE TABLE $TABLE_TODO (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                $USER_ID INTEGER,
                $TITLE_COL TEXT,
                $SUBJECT_COL TEXT,
                $SUBJECT_TYPE_COL TEXT,
                $DESCRIPTION_COL TEXT,
                $DIFFICULTY_COL TEXT,
                $ALARM_DATE_COL TEXT,
                $IS_DONE_COL INTEGER,
                $DUE_DATE_COL TEXT,
                FOREIGN KEY($USER_ID) REFERENCES $TABLE_USER(id) ON DELETE CASCADE,
                CONSTRAINT name_unique UNIQUE ($TITLE_COL)
                )""".trimIndent()
        db.execSQL(tableCreateUser)
        db.execSQL(tableCreateTodo)
    }

    override fun onUpgrade(db: SQLiteDatabase, p1: Int, p2: Int) {
        // this method is to check if table already exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO)
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER)
        onCreate(db)
    }
    // T
    fun createTodo(userId: Long,title: String, subject : String, subjectType: String, description: String,  difficulty: String, alarmDate: String, dueDate: String, isDone: Int) : Long{



        val values = ContentValues()


        values.put(USER_ID, userId)
        values.put(TITLE_COL, title)
        values.put(SUBJECT_COL, subject)
        values.put(SUBJECT_TYPE_COL, subjectType)
        values.put(DESCRIPTION_COL, description)
        values.put(DIFFICULTY_COL, difficulty)
        values.put(ALARM_DATE_COL, alarmDate)
        values.put(DUE_DATE_COL, dueDate)
        values.put(IS_DONE_COL, isDone)
        var result: Long = -1  // default false
        val db = this.writableDatabase
        try {
            // insert new row return the id
            result = db.insert(TABLE_TODO, null, values)
        } catch (e: Exception) {
            Log.d("LOG", e.message.toString())
        } finally {
            db.close() // close db
        }

        return result // -1 if not work
    }


    fun getTodo(sort: String = "Date", userId: Long): ArrayList<TodoModel>{
        val todoList = mutableListOf<TodoModel>()
        val db = this.readableDatabase
        val cursor: Cursor
        Log.d("DBLOG", "UserID: " + userId)

        if (sort == "Date"){
            cursor = db.rawQuery("SELECT * FROM $TABLE_TODO WHERE $USER_ID = ? ORDER BY $DUE_DATE_COL ASC", arrayOf(userId.toString()))
            while (cursor.moveToNext()){
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val userIdx = cursor.getLong(cursor.getColumnIndexOrThrow(USER_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE_COL))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(SUBJECT_TYPE_COL))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION_COL))
                val subject = cursor.getString(cursor.getColumnIndexOrThrow(SUBJECT_COL))
                val difficulty = cursor.getString(cursor.getColumnIndexOrThrow(DIFFICULTY_COL))
                val alarmDate = cursor.getString(cursor.getColumnIndexOrThrow(ALARM_DATE_COL))
                val dueDate = cursor.getString(cursor.getColumnIndexOrThrow(DUE_DATE_COL))
                val isDone = cursor.getInt(cursor.getColumnIndexOrThrow(IS_DONE_COL))
                val todo = TodoModel(id, userIdx, title, type, description, subject, difficulty, dueDate, alarmDate, isDone == 1)
                todoList.add(todo)

            }
            cursor.close()
        }else if (sort == "Subject Type"){
            cursor = db.rawQuery("SELECT * FROM $TABLE_TODO WHERE $USER_ID = ? ORDER BY $SUBJECT_COL ASC", arrayOf(userId.toString()))
            while (cursor.moveToNext()){
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val userIdx = cursor.getLong(cursor.getColumnIndexOrThrow(USER_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE_COL))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(SUBJECT_TYPE_COL))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION_COL))
                val subject = cursor.getString(cursor.getColumnIndexOrThrow(SUBJECT_COL))
                val difficulty = cursor.getString(cursor.getColumnIndexOrThrow(DIFFICULTY_COL))
                val alarmDate = cursor.getString(cursor.getColumnIndexOrThrow(ALARM_DATE_COL))
                val dueDate = cursor.getString(cursor.getColumnIndexOrThrow(DUE_DATE_COL))
                val isDone = cursor.getInt(cursor.getColumnIndexOrThrow(IS_DONE_COL))
                val todo = TodoModel(id, userIdx, title, type, description, subject, difficulty, dueDate, alarmDate, isDone == 1)
                todoList.add(todo)

            }
            cursor.close()
        }else if (sort == "Difficulty"){
            cursor = db.rawQuery("SELECT * FROM $TABLE_TODO WHERE $USER_ID = ? ORDER BY $SUBJECT_TYPE_COL ASC", arrayOf(userId.toString()))
            while (cursor.moveToNext()){
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val userIdx = cursor.getLong(cursor.getColumnIndexOrThrow(USER_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE_COL))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(SUBJECT_TYPE_COL))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION_COL))
                val subject = cursor.getString(cursor.getColumnIndexOrThrow(SUBJECT_COL))
                val difficulty = cursor.getString(cursor.getColumnIndexOrThrow(DIFFICULTY_COL))
                val alarmDate = cursor.getString(cursor.getColumnIndexOrThrow(ALARM_DATE_COL))
                val dueDate = cursor.getString(cursor.getColumnIndexOrThrow(DUE_DATE_COL))
                val isDone = cursor.getInt(cursor.getColumnIndexOrThrow(IS_DONE_COL))
                val todo = TodoModel(id,userIdx, title, type, description, subject, difficulty, dueDate, alarmDate, isDone == 1)
                todoList.add(todo)

            }
            cursor.close()
        }else{
            cursor = db.rawQuery("SELECT * FROM $TABLE_TODO", null)
            while (cursor.moveToNext()){
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val userIdx = cursor.getLong(cursor.getColumnIndexOrThrow(USER_ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(TITLE_COL))
                val type = cursor.getString(cursor.getColumnIndexOrThrow(SUBJECT_TYPE_COL))
                val description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION_COL))
                val subject = cursor.getString(cursor.getColumnIndexOrThrow(SUBJECT_COL))
                val difficulty = cursor.getString(cursor.getColumnIndexOrThrow(DIFFICULTY_COL))
                val alarmDate = cursor.getString(cursor.getColumnIndexOrThrow(ALARM_DATE_COL))
                val dueDate = cursor.getString(cursor.getColumnIndexOrThrow(DUE_DATE_COL))
                val isDone = cursor.getInt(cursor.getColumnIndexOrThrow(IS_DONE_COL))
                val todo = TodoModel(id, userIdx, title, type, description, subject, difficulty, dueDate, alarmDate, isDone == 1)
                todoList.add(todo)
            }
            cursor.close()
        }
        return ArrayList(todoList)
    }

    fun getSpecificTodo(title: String, userId: Long) : TodoModel? {
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_TODO WHERE $TITLE_COL = ? AND $USER_ID = ?", arrayOf(title.toString(), userId.toString()))
        var specificTodo: TodoModel? = null
        while (cursor.moveToNext()){
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val userIdx = cursor.getLong(cursor.getColumnIndexOrThrow(USER_ID))
            val titlex = cursor.getString(cursor.getColumnIndexOrThrow(TITLE_COL))
            val type = cursor.getString(cursor.getColumnIndexOrThrow(SUBJECT_TYPE_COL))
            val description = cursor.getString(cursor.getColumnIndexOrThrow(DESCRIPTION_COL))
            val subject = cursor.getString(cursor.getColumnIndexOrThrow(SUBJECT_COL))
            val difficulty = cursor.getString(cursor.getColumnIndexOrThrow(DIFFICULTY_COL))
            val alarmDate = cursor.getString(cursor.getColumnIndexOrThrow(ALARM_DATE_COL))
            val dueDate = cursor.getString(cursor.getColumnIndexOrThrow(DUE_DATE_COL))
            val isDone = cursor.getInt(cursor.getColumnIndexOrThrow(IS_DONE_COL))
            specificTodo = TodoModel(id, userIdx, titlex, type, description, subject, difficulty, dueDate, alarmDate, isDone == 1)
        }
        cursor.close()
        return specificTodo
    }
    fun scheduleTodoNotification(context: Context, todoId: Int, title: String, alarmTime: String) {
        try {

            val formatTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.getDefault())
            val date = formatTime.parse(alarmTime) ?: return

            // for converting the time user input to milisec
            val calendar = Calendar.getInstance().apply {
                timeInMillis = date.time
                // clear the  seconds and milliseconds
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val timeInMillis = calendar.timeInMillis
            Log.d("NOTIF", "Time in Milis" + timeInMillis.toString())
            // don't schedule if time has passed
            if (timeInMillis <= System.currentTimeMillis()) {
                Log.d("TodoAlarm", "Skipping past alarm for todo: $todoId")
                return
            }

            // apply is like para di na intent.putExtra
            val intent = Intent(context, AlarmReceiver::class.java).apply {
                putExtra("TODO_ID", todoId)
                putExtra("TODO_TITLE", title)
            }
            Log.d("NOTIF", "TODOID: " + todoId)
            Log.d("NOTIF", "TODOID: " + title)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                todoId, // request code for unique identifier for pending intents
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            // get alarm manager and schedule notif
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                timeInMillis,
                pendingIntent
            )
            Log.d("TodoAlarm", "Scheduled alarm for todo: $todoId at ${calendar.time}")
        } catch (e: Exception) {
            Log.e("TodoAlarm", "Error scheduling notification", e)
        }
    }


    fun updateTodo(originalTitle: String, title: String, subject : String, subjectType: String, description: String,  difficulty: String, alarmDate: String, dueDate: String, isDone: Int): Boolean {
        Log.d("HELP", "ORIG: " + originalTitle +" Title: " + title)

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(TITLE_COL, title)
        values.put(SUBJECT_COL, subject)
        values.put(SUBJECT_TYPE_COL, subjectType)
        values.put(DESCRIPTION_COL, description)
        values.put(DIFFICULTY_COL, difficulty)
        values.put(ALARM_DATE_COL, alarmDate)
        values.put(DUE_DATE_COL, dueDate)
        values.put(IS_DONE_COL, isDone)

        var result = -1
        try{
            result = db.update(TABLE_TODO, values, "$TITLE_COL = ?", arrayOf(originalTitle))
        }catch(e: Exception){
            Log.d("LOG", e.message.toString())
        }finally{
            db.close()
        }
        val resultActual: Boolean = result > 0
        return  resultActual;

    }
        // title to get the original row and isDone to change the checkbox status
    fun updateTodoStatus(title: String, isDone: Int): Boolean {
        Log.d("DBHELP", isDone.toString())

        val db = this.writableDatabase
        val values = ContentValues()
        values.put(IS_DONE_COL, isDone)

        var result = -1
        try{
            result = db.update(TABLE_TODO, values, "$TITLE_COL = ?", arrayOf(title))
        }catch(e: Exception){
            Log.d("LOG", e.message.toString())
        }finally{
            db.close()
        }
        val resultActual: Boolean = result > 0
        return  resultActual;

    }
    fun deleteTodo(title: String){

        val db = this.writableDatabase
        db.delete(TABLE_TODO,"$TITLE_COL = ?", arrayOf(title))
        db.close()
    }

    fun createUser(username: String, password : String) : Boolean{
        // below we are creating

        val values = ContentValues()

        // we are inserting our values
        values.put(USERNAME_COL, username)
        values.put(PASSWORD_COL, password)
        Log.d("signup", "username" + username)
        Log.d("signup", "password" + password)
        var result: Long = -1  // default false
        val db = this.writableDatabase
        try{
            result = db.insert(TABLE_USER, null, values)
        }catch(e: Exception){
            Log.d("LOG", e.message.toString())
        }finally{
            db.close()
        }
        return result != -1L // true if succesfull, false otherwise
    }

    fun verifyLogin(username: String, password: String): UserModel? {

        Log.d("DBLOG", username + password)
        val db = this.readableDatabase
        val cursor: Cursor = db.rawQuery("SELECT * FROM $TABLE_USER WHERE $USERNAME_COL = ? AND $PASSWORD_COL = ?", arrayOf(username, password))
        var specificUser: UserModel? = null
        while (cursor.moveToNext()){
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val passwordx = cursor.getString(cursor.getColumnIndexOrThrow(USERNAME_COL))
            val usernamex = cursor.getString(cursor.getColumnIndexOrThrow(PASSWORD_COL))
            specificUser = UserModel(id,usernamex,passwordx)
        }
        cursor.close()
        return specificUser
    }
    fun cancelTodoNotification(context: Context, todoId: Int) {
        val intent = Intent(context, AlarmReceiver::class.java).apply{
            putExtra("TODO_ID", todoId)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            todoId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // get alarm manager and schedule notif
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

}