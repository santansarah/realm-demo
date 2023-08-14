package com.santansarah.realmdemo.ui

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.santansarah.realmdemo.data.ContactRepository
import com.santansarah.realmdemo.data.models.Contact
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ContactsViewModel() : ViewModel() {

    var data = mutableStateOf(emptyList<Contact>())

    init {
        viewModelScope.launch {
            ContactRepository.getContacts().collect {
                Log.d("test", "emitting...")
                data.value = it
            }
        }
    }

}