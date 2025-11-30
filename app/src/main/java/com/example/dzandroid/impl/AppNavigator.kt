package com.example.dzandroid.impl

import android.content.Context
import android.content.Intent
import com.example.dzandroid.MainActivity
import com.example.dzandroid.profile.presentation.Navigator


class AppNavigator : Navigator {
    override fun createMainActivityIntent(context: Context): Intent {
        return Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
    }
}