package com.santansarah.realmdemo.data

import com.santansarah.realmdemo.data.models.Address
import com.santansarah.realmdemo.data.models.Contact
import com.santansarah.realmdemo.data.models.ContactMethod
import io.realm.kotlin.Realm
import io.realm.kotlin.RealmConfiguration

fun provideRealm(): Realm {
    val config = RealmConfiguration.Builder(
        schema = setOf(
            Contact::class, Address::class, ContactMethod::class
        )
    )
        .compactOnLaunch()
        .build()
    return Realm.open(config)
}