package com.example.phoneapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.phoneapp.presentation.ContactListScreen
import com.example.phoneapp.presentation.ContactsViewModel
import com.example.phoneapp.presentation.ViewModelFactory
import com.example.phoneapp.ui.theme.PhoneAppTheme
import javax.inject.Inject

class MainActivity : ComponentActivity() {

    val component by lazy {
        (this.application as ContactApp).appComponent
    }
    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    val contactsViewModel by lazy{
        ViewModelProvider(this, viewModelFactory)[ContactsViewModel::class]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        component.inject(this)


        setContent {
            PhoneAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContactListScreen(
                        modifier = Modifier.padding(innerPadding),
                        contactsViewModel = contactsViewModel
                    )
                }
            }
        }
    }
}