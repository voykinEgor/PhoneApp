package com.example.phoneapp.di

import android.content.Context
import com.example.phoneapp.MainActivity
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(app: MainActivity)

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance context: Context): AppComponent
    }
}