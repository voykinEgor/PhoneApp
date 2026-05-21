package com.example.phoneapp.presentation

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.example.phoneapp.domain.ContactListState
import androidx.core.net.toUri
import com.example.phoneapp.aidl.IDuplicateContactsCallback
import com.example.phoneapp.aidl.IDuplicateContactsService
import com.example.phoneapp.domain.DeleteDuplicatesStatus
import com.example.phoneapp.service.DuplicateContactsService

@Composable
fun ContactListScreen(
    modifier: Modifier = Modifier,
    contactsViewModel: ContactsViewModel,
) {
    val context = LocalContext.current
    val stateListContacts by contactsViewModel.contactList.collectAsState()

    val readPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            contactsViewModel.loadContacts()
        } else {
            contactsViewModel.onPermissionDenied()
        }
    }
    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            contactsViewModel.loadContacts()
        } else {
            readPermissionLauncher.launch(
                Manifest.permission.READ_CONTACTS
            )
        }
    }

    var duplicateContactsService by remember {
        mutableStateOf<IDuplicateContactsService?>(null)
    }

    val serviceConnection = remember {
        object : ServiceConnection {
            override fun onServiceConnected(
                p0: ComponentName?,
                p1: IBinder?
            ) {
                duplicateContactsService =
                    IDuplicateContactsService.Stub.asInterface(p1)
            }

            override fun onServiceDisconnected(p0: ComponentName?) {
                duplicateContactsService = null
            }

        }
    }

    val duplicateContactCallback = remember {
        object : IDuplicateContactsCallback.Stub() {
            override fun onCompleted(
                status: Int,
                detectedCount: Int,
                message: String?
            ) {
                Handler(Looper.getMainLooper()).post {
                    when (status) {
                        DeleteDuplicatesStatus.SUCCESS -> {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            contactsViewModel.loadContacts()
                        }

                        DeleteDuplicatesStatus.NOT_FOUND -> {
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                        }

                        DeleteDuplicatesStatus.ERROR -> {
                            Toast.makeText(
                                context,
                                message ?: "Error deleting duplicate contacts",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    fun deleteDuplicateContacts() {
        if (duplicateContactsService == null) {
            Toast.makeText(
                context,
                "Service is not connected",
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        duplicateContactsService?.deleteDuplicateContacts(duplicateContactCallback)
    }


    val writePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val readGranted = permissions[Manifest.permission.READ_CONTACTS] == true
        val writeGranted = permissions[Manifest.permission.WRITE_CONTACTS] == true

        if (readGranted && writeGranted) {
            deleteDuplicateContacts()
        } else {
            Toast.makeText(
                context,
                "Contacts permissions denied",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    DisposableEffect(Unit) {
        val intent = Intent(context, DuplicateContactsService::class.java)

        context.bindService(
            intent,
            serviceConnection,
            Context.BIND_AUTO_CREATE
        )

        onDispose {
            context.unbindService(serviceConnection)
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        when (stateListContacts) {
            ContactListState.Initial -> Unit
            ContactListState.Loading -> {
                LoadingContactsContent(modifier = Modifier.fillMaxSize())
            }

            is ContactListState.Content -> {
                val contactList = (stateListContacts as ContactListState.Content).contactsList

                if (contactList.contactsList.isEmpty()) {
                    EmptyContactsContent(modifier = Modifier.weight(1f))
                } else {
                    ContactListContent(
                        modifier = Modifier.fillMaxSize(),
                        contactList = contactList,
                        onDeleteDuplicatesClick = {
                            if (hasContactsPermissions(context)) {
                                deleteDuplicateContacts()
                            } else {
                                writePermissionLauncher.launch(
                                    arrayOf(
                                        Manifest.permission.READ_CONTACTS,
                                        Manifest.permission.WRITE_CONTACTS
                                    )
                                )
                            }
                        },
                        onContactClick = { contact ->
                            val dialIntent = Intent(
                                Intent.ACTION_DIAL,
                                "tel:${Uri.encode(contact.phoneNumber)}".toUri()
                            )
                            context.startActivity(dialIntent)
                        },
                    )
                }
            }

            is ContactListState.Error -> {
                ErrorContactsContent(
                    modifier = Modifier.weight(1f),
                    message = (stateListContacts as ContactListState.Error).errorMessage
                )
            }
        }
    }
}

@Composable
private fun LoadingContactsContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            modifier = Modifier.align(Alignment.Center),
            color = MaterialTheme.colorScheme.primaryContainer
        )
    }
}

@Composable
private fun EmptyContactsContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "No contacts",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Saved contacts will appear here",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorContactsContent(
    modifier: Modifier = Modifier,
    message: String
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "Something went wrong",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun hasContactsPermissions(context: Context): Boolean {
    val readGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.READ_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED

    val writeGranted = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_CONTACTS
    ) == PackageManager.PERMISSION_GRANTED

    return readGranted && writeGranted
}
