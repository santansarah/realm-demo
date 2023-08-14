package com.santansarah.realmdemo.data

import android.util.Log
import com.santansarah.realmdemo.data.models.Address
import com.santansarah.realmdemo.data.models.Contact
import com.santansarah.realmdemo.data.models.ContactMethod
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration
import io.realm.kotlin.migration.RealmMigration

fun provideRealm(): Realm {
    val config = RealmConfiguration.Builder(
        schema = setOf(
            Contact::class, Address::class, ContactMethod::class
        ),
    )
        .schemaVersion(2)
        .compactOnLaunch()
        .build()

    Log.d("test", "created Realm...")

    return Realm.open(config)
}
