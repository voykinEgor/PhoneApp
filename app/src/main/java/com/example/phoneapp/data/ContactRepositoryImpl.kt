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
    private val contentResolver: ContentResolver,
    private val contactMapper: ContactMapper
): ContactRepository {
    override fun getContactList(): Flow<ContactListState> = flow{
        emit(ContactListState.Loading)

        val contactDtos = mutableListOf<ContactDto>()
        val phoneUri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.TYPE,
            ContactsContract.CommonDataKinds.Phone.PHOTO_URI,
            ContactsContract.CommonDataKinds.Phone.IS_PRIMARY,
            ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY
        )

        contentResolver.query(
            phoneUri,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY} ASC"
        )?.use { cursor ->
            val contactIdIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID
            )
            val displayNameIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME_PRIMARY
            )
            val phoneNumberIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.NUMBER
            )
            val phoneTypeIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.TYPE
            )
            val photoUriIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.PHOTO_URI
            )
            val isPrimaryIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.IS_PRIMARY
            )
            val isSuperPrimaryIndex = cursor.getColumnIndexOrThrow(
                ContactsContract.CommonDataKinds.Phone.IS_SUPER_PRIMARY
            )

            while (cursor.moveToNext()) {
                val phoneNumber = cursor.getString(phoneNumberIndex)
                    ?.takeIf { it.isNotBlank() }
                    ?: continue

                contactDtos.add(
                    ContactDto(
                        contactId = cursor.getLong(contactIdIndex),
                        displayName = cursor.getString(displayNameIndex),
                        phoneNumber = phoneNumber,
                        phoneType = cursor.getInt(phoneTypeIndex),
                        photoUri = cursor.getString(photoUriIndex),
                        isPrimary = cursor.getInt(isPrimaryIndex) == 1,
                        isSuperPrimary = cursor.getInt(isSuperPrimaryIndex) == 1
                    )
                )
            }
        }

        val contacts = contactDtos
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