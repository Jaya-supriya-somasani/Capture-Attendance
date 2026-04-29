package com.example.userattendence

import android.app.Application
import com.example.userattendence.di.AppComponent
import com.example.userattendence.di.AppModule
import com.example.userattendence.di.DaggerAppComponent

class AttendanceApp : Application() {
    lateinit var appComponent: AppComponent
        private set

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}