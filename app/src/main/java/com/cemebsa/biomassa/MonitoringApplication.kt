package com.cemebsa.biomassa

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject


@HiltAndroidApp
class MonitoringApplication: Application(){
    @Inject lateinit var sharedPreferences: SharedPreferences

    override fun onCreate() {
        super.onCreate()

        val isNightMode: Boolean = sharedPreferences.getBoolean("NIGHT_MODE", false)

        if (isNightMode) {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

        } else {

            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        }
    }
}