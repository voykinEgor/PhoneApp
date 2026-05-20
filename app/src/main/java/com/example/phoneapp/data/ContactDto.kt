package com.example.phoneapp.data

data class ContactDto(
    val contactId: Long,
    val lookupKey: String?,
    val displayName: String?,
    val phoneNumber: String,
    val normalizedPhoneNumber: String?,
    val phoneType: Int?,
    val phoneLabel: String?,
    val photoUri: String?,
    val isPrimary: Boolean,
    val isSuperPrimary: Boolean
)