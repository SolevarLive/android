package com.example.dzandroid.profile.presentation

import android.content.Context
import android.content.Intent

interface Navigator {
    fun createMainActivityIntent(context: Context): Intent
}