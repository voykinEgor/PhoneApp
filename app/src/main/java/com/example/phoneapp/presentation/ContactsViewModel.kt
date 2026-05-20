package com.example.phoneapp.presentation

import android.content.ContentResolver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phoneapp.domain.ContactListState
import com.example.phoneapp.domain.ContactList
import com.example.phoneapp.domain.GetContactsListUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactsViewModel @Inject constructor(
    private val getContactsListUseCase: GetContactsListUseCase
): ViewModel() {

    private val _contactList = MutableStateFlow<ContactListState>(ContactListState.Initial)
    val contactList: StateFlow<ContactListState> = _contactList

    fun loadContacts() {
        viewModelScope.launch {
            getContactsListUseCase()
                .collect { state ->
                    _contactList.value = state
                }
        }
    }

    fun onPermissionDenied() {
        _contactList.value = ContactListState.Error("Contacts permission denied")
    }
}