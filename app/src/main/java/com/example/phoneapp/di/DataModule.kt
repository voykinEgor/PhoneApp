package com.example.phoneapp.di

import android.content.ContentResolver
import android.content.Context
import com.example.phoneapp.data.ContactRepositoryImpl
import com.example.phoneapp.domain.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindUserDeviceRepository(impl: ContactRepositoryImpl): ContactRepository

    companion object {
        @Provides
        fun provideContentResolver(context: Context): ContentResolver {
            return context.contentResolver
        }
    }
}