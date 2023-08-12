package com.santansarah.realmdemo.data

import android.util.Log
import com.santansarah.realmdemo.data.models.Contact
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import io.realm.kotlin.notifications.InitialResults
import io.realm.kotlin.notifications.UpdatedResults
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import org.mongodb.kbson.ObjectId

class ContactRepository(private val realm: Realm) {

    fun fetchInitialList(): List<Contact> {
        return realm.query<Contact>().find().toList()
    }

    fun getContacts(): Flow<List<Contact>> {
        return realm.query<Contact>().asFlow().filterNot {
            it is InitialResults<Contact>
        }.map {
            it.list
        }
    }

    suspend fun saveContact(contact: Contact) {
        Log.d("test", "saving....")
        try {
            realm.write {
                copyToRealm(contact, UpdatePolicy.ALL)
            }
        } catch (e: Exception) {
            Log.d("test", e.message.toString())
        }
    }

    suspend fun deleteContact(id: ObjectId) {
        Log.d("test", "deleting...")
        realm.write {
            val existingContact = query<Contact>(query = "_id == $0", id).first().find()
            try {
                existingContact?.let { delete(it) }
            } catch (e: Exception) {
                Log.d("ContactRepo", "${e.message}")
            }
        }
    }
}