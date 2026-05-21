package com.example.phoneapp.aidl;

import com.example.phoneapp.aidl.IDuplicateContactsCallback;

interface IDuplicateContactsService {
    void deleteDuplicateContacts(IDuplicateContactsCallback callback);
}