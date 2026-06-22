package com.venturedive.tasksapp.testing

import com.venturedive.tasksapp.data.repository.ProfileRepository
import com.venturedive.tasksapp.domain.model.Profile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeProfileRepository(initial: Profile?) : ProfileRepository {

    private val flow = MutableStateFlow(initial)

    fun current(): Profile? = flow.value

    override fun observeProfile(): Flow<Profile?> = flow

    override suspend fun updateProfile(profile: Profile) {
        flow.value = profile
    }
}
