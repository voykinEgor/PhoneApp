package com.example.phoneapp.domain

interface ContactRepository {

    fun getContactList(): ContactList

}