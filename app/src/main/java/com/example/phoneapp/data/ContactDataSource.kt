package com.example.phoneapp.data

interface ContactDataSource {
    fun getContactDtos(): List<ContactDto>
}