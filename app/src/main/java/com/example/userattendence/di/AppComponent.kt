package com.example.userattendence.di

import com.example.userattendence.data.repository.AttendanceRepository
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    fun repository(): AttendanceRepository
}