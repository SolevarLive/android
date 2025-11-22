package com.example.dzandroid.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.dzandroid.data.models.Profile
import com.google.gson.Gson
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.profileDataStore: DataStore<Preferences> by preferencesDataStore(name = "profile")

class ProfileDataStore(private val context: Context) {

    companion object {
        private val PROFILE_KEY = stringPreferencesKey("profile_data")
    }

    private val gson = Gson()

    val profileFlow: Flow<Profile> = context.profileDataStore.data
        .map { preferences ->
            val profileJson = preferences[PROFILE_KEY]
            if (profileJson != null) {
                gson.fromJson(profileJson, Profile::class.java)
            } else {
                Profile()
            }
        }

    suspend fun saveProfile(profile: Profile) {
        context.profileDataStore.edit { preferences ->
            val profileJson = gson.toJson(profile)
            preferences[PROFILE_KEY] = profileJson
        }
    }
}