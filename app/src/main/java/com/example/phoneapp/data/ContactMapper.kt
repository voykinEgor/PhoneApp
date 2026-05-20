package com.example.phoneapp.data

import com.example.phoneapp.domain.Contact
import javax.inject.Inject

class ContactMapper @Inject constructor() {

    fun mapContactDtoToDomain(contactDto: ContactDto): Contact{
        return Contact(
            id = contactDto.contactId,
            name = contactDto.displayName ?: "Unknown",
            phoneNumber = contactDto.phoneNumber,
            photoUri = contactDto.photoUri
        )
    }

}
