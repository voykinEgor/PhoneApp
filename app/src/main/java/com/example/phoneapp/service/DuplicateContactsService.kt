package com.example.phoneapp.service

import android.app.Service
import android.content.ContentProviderOperation
import android.content.Intent
import android.os.IBinder
import android.provider.ContactsContract
import com.example.phoneapp.ContactApp
import com.example.phoneapp.aidl.IDuplicateContactsCallback
import com.example.phoneapp.aidl.IDuplicateContactsService
import com.example.phoneapp.data.ContactDataSource
import com.example.phoneapp.domain.DeleteDuplicatesStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

class DuplicateContactsService: Service() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    val component by lazy {
        (this.application as ContactApp).appComponent
    }

    @Inject
    lateinit var contactDataSource: ContactDataSource

    @Inject
    lateinit var duplicateContactMapper: DuplicateContactMapper

    private val binder = object : IDuplicateContactsService.Stub(){
        override fun deleteDuplicateContacts(callback: IDuplicateContactsCallback) {
            serviceScope.launch {
                try {
                    val deletedCount = deleteDuplicates()

                    val status = if (deletedCount > 0){
                        DeleteDuplicatesStatus.SUCCESS
                    }else{
                        DeleteDuplicatesStatus.NOT_FOUND
                    }

                    val message = if (deletedCount > 0) "Duplicate contacts deleted" else "Duplicate contacts not found"

                    callback.onCompleted(status, deletedCount, message)
                }catch (e: Exception){
                    callback.onCompleted(
                        DeleteDuplicatesStatus.ERROR,
                        0,
                        e.message
                    )
                }
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        component.inject(this)
    }

    override fun onBind(p0: Intent?): IBinder {
        return binder
    }

    override fun onDestroy() {
        serviceScope.cancel()
        super.onDestroy()
    }

    private fun deleteDuplicates(): Int{
        val contacts = contactDataSource.getContactDtos()
        val defaultRegion = Locale.getDefault().country
        val duplicateContactsToDelete = contacts
            .groupBy { duplicateContactMapper.normalizeAndMapDtoToDuplicateContactKey(it, defaultRegion) }
            .values
            .filter { it.size > 1 }
            .flatMap { group ->
                group.sortedBy { it.contactId }.drop(1)
            }
            .distinctBy { it.contactId }

        if (duplicateContactsToDelete.isEmpty())
            return 0

        val operations = duplicateContactsToDelete.map { contact ->
            ContentProviderOperation
                .newDelete(ContactsContract.RawContacts.CONTENT_URI)
                .withSelection(
                    "${ContactsContract.RawContacts.CONTACT_ID} = ?",
                    arrayOf(contact.contactId.toString())
                )
                .build()
        }

        contentResolver.applyBatch(
            ContactsContract.AUTHORITY,
            ArrayList(operations)
        )

        return duplicateContactsToDelete.size
    }
}