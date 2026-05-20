package com.example.phoneapp.data

import com.example.phoneapp.domain.Contact

class ContactMapper {

    fun mapContactDtoToDomain(contactDto: ContactDto): Contact{
        return Contact(
            name = contactDto.displayName ?: "Unknown",
            phoneNumber = contactDto.phoneNumber
        )
    }

}