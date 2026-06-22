package com.venturedive.tasksapp.data.repository

import com.venturedive.tasksapp.core.di.IoDispatcher
import com.venturedive.tasksapp.data.local.db.DEFAULT_PROFILE_ID
import com.venturedive.tasksapp.data.local.db.ProfileDao
import com.venturedive.tasksapp.data.local.db.ProfileEntity
import com.venturedive.tasksapp.domain.model.Profile
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Source of truth for the user's profile; exposes it as a [Flow] for ViewModels to collect. */
interface ProfileRepository {
    fun observeProfile(): Flow<Profile?>
    suspend fun updateProfile(profile: Profile)
}

class ProfileRepositoryImpl @Inject constructor(
    private val dao: ProfileDao,
    @IoDispatcher private val io: CoroutineDispatcher,
) : ProfileRepository {

    override fun observeProfile(): Flow<Profile?> =
        dao.observe(DEFAULT_PROFILE_ID).map { it?.toDomain() }

    override suspend fun updateProfile(profile: Profile) =
        withContext(io) { dao.upsert(profile.toEntity()) }
}

private fun ProfileEntity.toDomain() = Profile(id, name, email, bio, avatarUri)
private fun Profile.toEntity() = ProfileEntity(id, name, email, bio, avatarUri)
