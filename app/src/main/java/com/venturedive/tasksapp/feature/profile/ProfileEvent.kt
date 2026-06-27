package com.venturedive.tasksapp.feature.profile

sealed interface ProfileEvent {
    data object Saved : ProfileEvent
}