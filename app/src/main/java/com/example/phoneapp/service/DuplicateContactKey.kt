package com.example.phoneapp.service

import com.example.phoneapp.data.ContactDto

data class DuplicateContactKey (
    val displayName: String?,
    val phoneNumber: String,
    val phoneType: Int?,
    val photoUri: String?,
    val isPrimary: Boolean,
    val isSuperPrimary: Boolean
)