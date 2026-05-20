package com.example.phoneapp.domain

sealed class ContactListState {
    object Initial: ContactListState()
    object Loading: ContactListState()
    data class Content(val contactsList: ContactList): ContactListState()
    data class Error(val errorMessage: String): ContactListState()
}