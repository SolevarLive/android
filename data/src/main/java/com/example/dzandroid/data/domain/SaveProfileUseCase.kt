package com.example.dzandroid.data.domain

import com.example.dzandroid.data.local.datastore.ProfileDataStore
import com.example.dzandroid.data.models.Profile

class SaveProfileUseCase(
    private val profileDataStore: ProfileDataStore
) {
    suspend operator fun invoke(profile: Profile) {
        profileDataStore.saveProfile(profile)
    }
}