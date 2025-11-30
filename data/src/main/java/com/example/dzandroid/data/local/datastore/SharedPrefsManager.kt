package com.example.dzandroid.data.local.datastore

import android.content.Context
import android.content.SharedPreferences

class SharedPrefsManager(private val context: Context) {

    companion object {
        private const val PREFS_NAME = "filter_prefs"
        private const val KEY_LANGUAGE = "language_key"
        private const val KEY_MIN_RATING = "min_rating_key"
        private const val KEY_RECIPE_NAME = "recipe_name_key"
    }

    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    var language: String
        get() = prefs.getString(KEY_LANGUAGE, "") ?: ""
        set(value) = prefs.edit().putString(KEY_LANGUAGE, value).apply()

    var minRating: Int
        get() = prefs.getInt(KEY_MIN_RATING, 0)
        set(value) = prefs.edit().putInt(KEY_MIN_RATING, value).apply()

    var recipeName: String
        get() = prefs.getString(KEY_RECIPE_NAME, "") ?: ""
        set(value) = prefs.edit().putString(KEY_RECIPE_NAME, value).apply()

    fun saveFilters(language: String, minRating: Int, recipeName: String) {
        prefs.edit().apply {
            putString(KEY_LANGUAGE, language)
            putInt(KEY_MIN_RATING, minRating)
            putString(KEY_RECIPE_NAME, recipeName)
        }.apply()
    }

    fun saveFiltersSync(language: String, minRating: Int, recipeName: String): Boolean {
        return prefs.edit().apply {
            putString(KEY_LANGUAGE, language)
            putInt(KEY_MIN_RATING, minRating)
            putString(KEY_RECIPE_NAME, recipeName)
        }.commit()
    }
}