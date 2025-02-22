package com.example.mobdeve

data class TodoModel(
    var id: Long,
    var user_id: Long,
    var title : String,
    var type: String,
    var description: String,
    var subject: String,
    var difficulty: String,
    var dueDate: String,
    var AlarmDate: String,
    var isFinished: Boolean
)

