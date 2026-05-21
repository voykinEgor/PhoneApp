package com.example.phoneapp.data

import android.content.ContentResolver
import android.database.Cursor
import android.provider.ContactsContract
import com.example.phoneapp.domain.ContactList
import com.example.phoneapp.domain.ContactListState
import com.example.phoneapp.domain.ContactRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

class ContactRepositoryImpl @Inject constructor(
    private val contactDataSource: ContactDataSource,
    private val contactMapper: ContactMapper
): ContactRepository {
    override fun getContactList(): Flow<ContactListState> = flow{
        emit(ContactListState.Loading)

        val contacts = contactDataSource.getContactDtos()
            .groupBy { it.contactId }
            .values
            .map { contactPhones -> contactPhones.maxBy { it.mainPhonePriority() } }
            .sortedBy { it.displayName.orEmpty() }
            .map(contactMapper::mapContactDtoToDomain)

        emit(ContactListState.Content(ContactList(contacts)))
    }.catch { error ->
        emit(ContactListState.Error(error.message ?: "Error receiving contacts"))
    }.flowOn(Dispatchers.IO)
}

private fun ContactDto.mainPhonePriority(): Int {
    return when {
        isSuperPrimary -> 3
        isPrimary -> 2
        phoneType == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE -> 1
        else -> 0
    }
}