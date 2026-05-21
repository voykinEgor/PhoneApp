package com.example.phoneapp.di

import android.content.Context
import com.example.phoneapp.MainActivity
import com.example.phoneapp.service.DuplicateContactsService
import dagger.BindsInstance
import dagger.Component

@ApplicationScope
@Component(modules = [DataModule::class, ViewModelModule::class])
interface AppComponent {

    fun inject(app: MainActivity)

    fun inject(service: DuplicateContactsService)

    @Component.Factory
    interface Factory{
        fun create(@BindsInstance context: Context): AppComponent
    }
}