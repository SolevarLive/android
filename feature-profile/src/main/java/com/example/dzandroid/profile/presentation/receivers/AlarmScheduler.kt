package com.example.dzandroid.profile.presentation.receivers

import android.content.Context

interface AlarmScheduler {
    fun scheduleReminder(context: Context, fullName: String, time: String)
    fun cancelReminder(context: Context)
}