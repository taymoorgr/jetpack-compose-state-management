package com.venturedive.tasksapp.domain.model

data class Profile(
    val id: Long,
    val name: String,
    val email: String,
    val bio: String = "",
    val avatarUri: String? = null
)
