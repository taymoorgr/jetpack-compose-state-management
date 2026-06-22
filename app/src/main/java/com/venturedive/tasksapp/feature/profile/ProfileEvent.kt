package com.venturedive.tasksapp.feature.profile

// One-time event, not state - so it can't replay on rotation.
sealed interface ProfileEvent {
    data object Saved : ProfileEvent
}