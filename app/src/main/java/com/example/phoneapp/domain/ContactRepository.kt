package com.example.phoneapp.domain

import com.example.phoneapp.domain.ContactListState
import kotlinx.coroutines.flow.Flow

interface ContactRepository {

    fun getContactList(): Flow<ContactListState>

}