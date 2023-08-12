package com.santansarah.realmdemo.data

import android.util.Log
import com.santansarah.realmdemo.data.models.Contact
import io.realm.kotlin.Realm
import io.realm.kotlin.UpdatePolicy
import io.realm.kotlin.ext.query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import org.mongodb.kbson.ObjectId

class ContactRepository(private val realm: Realm) {
    fun getContacts(): Flow<List<Contact>> {
        return realm.query<Contact>().asFlow().map { it.list }
    }

    suspend fun saveContact(contact: Contact) {
        try {
            realm.write {
                copyToRealm(contact, UpdatePolicy.ALL)
            }
        } catch (e: Exception) {
            Log.d("test", e.message.toString())
        }
    }

    suspend fun deleteContact(id: ObjectId) {
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