package com.example.phoneapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.phoneapp.domain.Contact
import com.example.phoneapp.domain.ContactList
import com.example.phoneapp.presentation.ContactListContent
import com.example.phoneapp.ui.theme.PhoneAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val listContacts = ContactList(listOf(Contact("Иван Иванов", "89536417453"), Contact("Иван Иванов", "89536417454")))
        setContent {
            PhoneAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ContactListContent(listContacts, Modifier.padding(innerPadding))
                }
            }
        }
    }
}