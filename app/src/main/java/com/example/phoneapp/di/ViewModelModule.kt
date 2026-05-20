package com.example.phoneapp.di

import androidx.lifecycle.ViewModel
import com.example.phoneapp.presentation.ContactsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ContactsViewModel::class)
    fun bindContactsViewModel(impl: ContactsViewModel): ViewModel
}