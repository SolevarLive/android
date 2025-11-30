package com.example.dzandroid.data.domain

import com.example.dzandroid.data.local.datastore.ProfileDataStore
import com.example.dzandroid.data.models.Profile
import kotlinx.coroutines.flow.Flow

class GetProfileUseCase(
    private val profileDataStore: ProfileDataStore
) {
    operator fun invoke(): Flow<Profile> {
        return profileDataStore.profileFlow
    }
}