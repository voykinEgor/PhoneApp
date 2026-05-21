package com.example.phoneapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.phoneapp.domain.ContactListState
import com.example.phoneapp.domain.GetContactsListUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class ContactsViewModel @Inject constructor(
    private val getContactsListUseCase: GetContactsListUseCase
): ViewModel() {

    private val _contactList = MutableStateFlow<ContactListState>(ContactListState.Initial)
    val contactList: StateFlow<ContactListState> = _contactList
    private var loadContactsJob: Job? = null

    fun loadContacts() {
        if (loadContactsJob?.isActive == true) return

        loadContactsJob = viewModelScope.launch {
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