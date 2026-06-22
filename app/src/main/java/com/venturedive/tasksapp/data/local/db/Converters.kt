package com.venturedive.tasksapp.data.local.db

import androidx.room.TypeConverter
import com.venturedive.tasksapp.domain.model.Priority

class Converters {
    @TypeConverter
    fun priorityToName(priority: Priority): String = priority.name

    @TypeConverter
    fun nameToPriority(name: String): Priority = Priority.valueOf(name)
}
