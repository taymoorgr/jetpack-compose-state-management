package com.venturedive.tasksapp.data.local.db

import androidx.room.Entity
import androidx.room.PrimaryKey

const val DEFAULT_PROFILE_ID = 1L

@Entity(tableName = "profile")
data class ProfileEntity(
    @PrimaryKey val id: Long = DEFAULT_PROFILE_ID,
    val name: String,
    val email: String,
    val bio: String = "",
    val avatarUri: String? = null
)
