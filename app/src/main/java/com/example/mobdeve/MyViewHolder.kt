package com.example.mobdeve

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.mobdeve.databinding.TodoLayoutBinding

class MyViewHolder(private val viewBinding: TodoLayoutBinding, private val listener: checkListenerCb) : RecyclerView.ViewHolder(viewBinding.root) {
    fun bindData(todo: TodoModel){
        viewBinding.tvMainTitle.text = todo.title
        viewBinding.tvMainType.text = todo.type
        viewBinding.tvMainDescription.text = todo.description
        viewBinding.tvMainSubject.text = todo.subject
        viewBinding.tvMainDifficulty.text = todo.difficulty
        viewBinding.tvMainAlarmDate.text = todo.AlarmDate
        viewBinding.tvMainDueDate.text = todo.dueDate
        viewBinding.cbStatus.isChecked = todo.isFinished


        viewBinding.cbStatus.setOnCheckedChangeListener{_, isChecked ->
            todo.isFinished = isChecked
            val newStatus: Int = if (isChecked) 1 else 0 // convert bool to int
            listener.onTodoCheckedChange(todo, newStatus) // update database isdone
        }

    }


}
