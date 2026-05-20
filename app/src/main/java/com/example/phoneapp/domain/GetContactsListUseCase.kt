package com.example.phoneapp.domain

import com.example.phoneapp.domain.ContactListState
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetContactsListUseCase @Inject constructor(
    private val contactRepository: ContactRepository
) {
    operator fun invoke(): Flow<ContactListState> = contactRepository.getContactList()
}