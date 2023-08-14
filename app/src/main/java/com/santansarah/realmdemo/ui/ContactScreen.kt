package com.santansarah.realmdemo.ui

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.santansarah.realmdemo.data.ContactRepository
import com.santansarah.realmdemo.data.models.Address
import com.santansarah.realmdemo.data.models.Contact
import com.santansarah.realmdemo.data.models.ContactMethod
import com.santansarah.realmdemo.data.models.ContactType
import com.santansarah.realmdemo.data.provideRealm
import kotlinx.coroutines.launch
import org.mongodb.kbson.ObjectId
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.reflect.KSuspendFunction1

@Composable
fun ContactScreen(
    viewModel: ContactsViewModel
) {

    val contacts by viewModel.data
    val scope = rememberCoroutineScope()

    var selectedContact: Contact? by remember {
        mutableStateOf(
            null
        )
    }

    ContactScreenLayout(
        contacts,
        selectedContact,
        {
            scope.launch {
                ContactRepository.saveContact(it)
                selectedContact = null
            }
        },
        {
            scope.launch {
                ContactRepository.deleteContact(it)
                selectedContact = null
            }
        },
        { selectedContact = it },
        { selectedContact = null }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ContactScreenLayout(
    contacts: List<Contact>,
    selectedContact: Contact?,
    onSave: (Contact) -> Unit,
    onDelete: (ObjectId) -> Unit,
    onSelect: (Contact) -> Unit,
    onCancel: () -> Unit
) {

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    onSelect(Contact().apply {
                        address = Address()
                        contactMethod = ContactMethod()
                    })
                }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "New Contact")
            }

            Spacer(modifier = Modifier.height(20.dp))

        }
    ) { padding ->

        Column(
            Modifier
                .fillMaxSize()
                .padding(
                    top = padding.calculateTopPadding(), start = 16.dp,
                    end = 16.dp
                ),
        ) {

            Text(
                modifier = Modifier.padding(bottom = 16.dp),
                text = "Your Contacts",
                style = MaterialTheme.typography.displaySmall
            )

            LazyColumn() {
                items(contacts) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp)
                            .clickable {
                                onSelect(it)
                            },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = (it.firstName + " " + it.lastName).uppercase())
                        IconButton(onClick = {
                            onDelete(it._id)
                        }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedContact != null) {
                ContactFields(
                    contact = selectedContact,
                    onValueChanged = {
                        onSelect(it)
                    },
                    onSave,
                    onCancel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFields(
    contact: Contact,
    onValueChanged: (Contact) -> Unit,
    onSave: (Contact) -> Unit,
    onCancel: () -> Unit
) {

    val updatableContact =
        Contact().apply {
            _id = contact._id
            address = Address().apply {
                cityName = contact.address?.cityName ?: ""
            }
            contactMethod = ContactMethod().apply {
                methodValue = contact.contactMethod?.methodValue ?: ""
                methodType = contact.contactMethod?.methodType ?: ContactType.EMAIL
            }
            firstName = contact.firstName
            lastName = contact.lastName
        }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = contact.firstName,
            onValueChange = {
                updatableContact.firstName = it
                onValueChanged(updatableContact)
            },
            placeholder = { Text(text = "First Name") }
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = contact.lastName,
            onValueChange = {
                updatableContact.lastName = it
                onValueChanged(updatableContact)
            },
            placeholder = { Text(text = "Last Name") }
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = contact.address?.cityName ?: "",
            onValueChange = {
                updatableContact.address?.cityName = it
                onValueChanged(updatableContact)
            },
            placeholder = { Text(text = "City") }
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            modifier = Modifier
                .padding(vertical = 6.dp)
                .fillMaxWidth(),
            text = "Contact Method",
            style = MaterialTheme.typography.titleLarge
        )

        ContactMethodFields(contact, onValueChanged, updatableContact)

        Spacer(modifier = Modifier.height(6.dp))

        val lastModified = SimpleDateFormat("MM/dd/yy hh:mm a", Locale.getDefault())
            .format(Date(contact.timestamp.epochSeconds * 1000)).uppercase()

        Text(
            text = "Last Modified: $lastModified",
            style = MaterialTheme.typography.labelMedium
        )

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onSave(updatableContact)
            }) {
            Text(text = "Insert or Update")
        }

        Spacer(modifier = Modifier.height(6.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                onCancel()
            }) {
            Text(text = "Cancel")
        }
    }
}

@Composable
@OptIn(ExperimentalMaterial3Api::class)
private fun ContactMethodFields(
    contact: Contact,
    onValueChanged: (Contact) -> Unit,
    updatableContact: Contact
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            expanded = !expanded
        }
    ) {

        OutlinedTextField(
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            value = contact.contactMethod?.methodType?.name ?: ContactType.EMAIL.name,
            onValueChange = {
                onValueChanged(updatableContact)
            },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            }
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            ContactType.values().forEach { methodType ->
                DropdownMenuItem(
                    text = { Text(text = methodType.name) },
                    onClick = {
                        updatableContact.contactMethod?.methodType = methodType
                        onValueChanged(updatableContact)
                        expanded = false
                    })
            }
        }
    }
    Spacer(modifier = Modifier.height(6.dp))
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = contact.contactMethod?.methodValue ?: "",
        onValueChange = {
            updatableContact.contactMethod?.methodValue = it
            onValueChanged(updatableContact)
        },
        placeholder = { Text(text = "Email or Phone") }
    )
    Spacer(modifier = Modifier.height(6.dp))

}

@Preview(showSystemUi = true)
@Composable
fun PreviewInsertContact() {

    ContactScreenLayout(contacts = listOf(
        Contact().apply {
            firstName = "Sarah"
            lastName = "Brenner"
            address = Address()
            contactMethod = ContactMethod()
        },
        Contact().apply {
            firstName = "Second"
            lastName = "Contact"
            address = Address()
            contactMethod = ContactMethod()
        }
    ),
        Contact().apply {
            firstName = "Sarah"
            lastName = "Brenner"
            address = Address()
            contactMethod = ContactMethod()
        },
        onSave = {}, onDelete = {}, onSelect = {}, onCancel = {})
}