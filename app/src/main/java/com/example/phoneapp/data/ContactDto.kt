package com.example.phoneapp.data

data class ContactDto(
    val contactId: Long,
    val displayName: String?,
    val phoneNumber: String,
    val phoneType: Int?,
    val photoUri: String?,
    val isPrimary: Boolean,
    val isSuperPrimary: Boolean
)