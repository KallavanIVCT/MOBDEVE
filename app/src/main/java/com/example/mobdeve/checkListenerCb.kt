package com.example.mobdeve

interface checkListenerCb {
    fun onTodoCheckedChange(todo: TodoModel, isChecked: Int)
}