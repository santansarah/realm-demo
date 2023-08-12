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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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

@Composable
fun ContactScreen() {

    val contactsRepo = ContactRepository(provideRealm())
    val contacts = contactsRepo.getContacts()
        .collectAsState(initial = emptyList())

    //Log.d("test", "got here...")

    // using this b/c i don't have a viewmodel
    var selectedContact by remember {
        mutableStateOf(
            Contact().apply {
                address = Address()
                contactMethod = ContactMethod()
            }
        )
    }

    val scope = rememberCoroutineScope()

    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {

        Text(
            modifier = Modifier.padding(bottom = 16.dp),
            text = "Your Contacts",
            style = MaterialTheme.typography.displaySmall
        )

        LazyColumn() {
            items(contacts.value) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp)
                        .clickable {
                            selectedContact = it
                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = (it.firstName + " " + it.lastName).uppercase())
                    IconButton(onClick = {
                        scope.launch {
                            contactsRepo.deleteContact(it._id)
                        }
                    }) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        ContactFields(
            contact = selectedContact,
            onValueChanged = {
                Log.d("test", "onValueChangedCalled: ${it.firstName}")
                selectedContact = it
            }
        ) {
            scope.launch {
                contactsRepo.saveContact(selectedContact)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactFields(
    contact: Contact,
    onValueChanged: (Contact) -> Unit,
    onSave: () -> Unit
) {

    // this is life with no viewmodel or dataclass.copy!!
    val updatableContact = Contact().apply {
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

    Column {
        OutlinedTextField(
            value = contact.firstName,
            onValueChange = {
                updatableContact.firstName = it
                onValueChanged(updatableContact)
            },
            placeholder = { Text(text = "First Name") }
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = contact.lastName,
            onValueChange = {
                updatableContact.lastName = it
                onValueChanged(updatableContact)
            },
            placeholder = { Text(text = "Last Name") }
        )
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = contact.address?.cityName ?: "",
            onValueChange = {
                updatableContact.address?.cityName = it
                onValueChanged(updatableContact)
            },
            placeholder = { Text(text = "City") }
        )
        Spacer(modifier = Modifier.height(6.dp))
        Divider(modifier = Modifier.fillMaxWidth(), thickness = 2.dp, color = Color.Black)
        Spacer(modifier = Modifier.height(6.dp))


        OutlinedTextField(
            value = contact.contactMethod?.methodValue ?: "",
            onValueChange = {
                updatableContact.contactMethod?.methodValue = it
                onValueChanged(updatableContact)
            },
            placeholder = { Text(text = "Email or Phone") }
        )
        Spacer(modifier = Modifier.height(6.dp))

        ContactMethodFields(contact, onValueChanged, updatableContact)

        Spacer(modifier = Modifier.height(6.dp))
        Button(onClick = {
            onSave()
        }) {
            Text(text = "Insert or Update")
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
                .menuAnchor(),
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
}

@Preview(showSystemUi = true)
@Composable
fun PreviewInsertContact() {

    val newContact by remember {
        mutableStateOf(
            Contact().apply {
                address = Address()
                contactMethod = ContactMethod()
            }
        )
    }

    ContactFields(newContact, {}, {})
}