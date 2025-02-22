package com.example.mobdeve

import android.app.AlertDialog
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.example.mobdeve.databinding.TodoLayoutBinding

class MyAdapter(private val data: ArrayList<TodoModel>, private val myActivityResultLauncher: ActivityResultLauncher<Intent>, val listener: checkListenerCb, val dbHelper: DBHelper): Adapter<MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemViewBinding: TodoLayoutBinding = TodoLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false )
        return MyViewHolder(itemViewBinding, listener)
    }

    override fun getItemCount(): Int {
        return data.size

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.bindData(data[position])
        holder.itemView.setOnClickListener(){

            val intent = Intent(holder.itemView.context,  EditTodoActivity::class.java)
            intent.putExtra("inputIDTodo", data[position].id)
            intent.putExtra("inputTodoID", data[position].user_id)
            intent.putExtra("inputTitle", data[position].title)
            intent.putExtra("inputPosition", position)
            intent.putExtra("inputSubject", data[position].subject)
            intent.putExtra("inputSubjectType", data[position].type)
            intent.putExtra("inputDescription", data[position].description)
            intent.putExtra("inputDifficulty", data[position].difficulty)
            intent.putExtra("inputAlarmDate", data[position].AlarmDate)
            intent.putExtra("inputDueDate", data[position].dueDate)
            intent.putExtra("inputIsDone", data[position].isFinished)
            Log.d("GR", data[position].isFinished.toString())
            myActivityResultLauncher.launch(intent)
        }
        /*
        val delBtn: Button = holder.itemView.findViewById(R.id.btnDelete)
        delBtn.setOnClickListener(){
            dbHelper.deleteTodo(data[position].title)
            dbHelper.cancelTodoNotification(holder.itemView.context, data[position].id.toInt())
            data.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, data.size)

        }
         */
        val delBtn: Button = holder.itemView.findViewById(R.id.btnDelete)
        delBtn.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)
            builder.setMessage("Are you sure you want to delete?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    dbHelper.deleteTodo(data[position].title)
                    dbHelper.cancelTodoNotification(holder.itemView.context, data[position].id.toInt())
                    data.removeAt(position)
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, data.size)
                }
                .setNegativeButton("No") { dialog, id ->
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }

    }
}