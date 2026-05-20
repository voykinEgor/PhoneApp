package com.example.phoneapp

import android.app.Application
import com.example.phoneapp.di.AppComponent
import com.example.phoneapp.di.DaggerAppComponent

class ContactApp: Application() {
    val appComponent: AppComponent by lazy {
        DaggerAppComponent.factory().create(this)
    }
}