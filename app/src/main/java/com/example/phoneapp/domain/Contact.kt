package com.example.phoneapp.domain

data class Contact(
    val id: Long,
    val name: String,
    val phoneNumber: String,
    val photoUri: String?
)