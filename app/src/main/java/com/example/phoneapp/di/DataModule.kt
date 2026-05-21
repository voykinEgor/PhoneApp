package com.example.phoneapp.di

import android.content.ContentResolver
import android.content.Context
import com.example.phoneapp.data.ContactDataSource
import com.example.phoneapp.data.ContactRepositoryImpl
import com.example.phoneapp.data.ContactsDataSourceImpl
import com.example.phoneapp.domain.ContactRepository
import dagger.Binds
import dagger.Module
import dagger.Provides

@Module
interface DataModule {

    @ApplicationScope
    @Binds
    fun bindUserDeviceRepository(impl: ContactRepositoryImpl): ContactRepository

    @ApplicationScope
    @Binds
    fun bindContactDataSource(impl: ContactsDataSourceImpl): ContactDataSource

    companion object {
        @Provides
        fun provideContentResolver(context: Context): ContentResolver {
            return context.contentResolver
        }
    }
}