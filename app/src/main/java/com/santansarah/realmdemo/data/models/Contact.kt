package com.santansarah.realmdemo.data.models

import io.realm.kotlin.types.RealmInstant
import io.realm.kotlin.types.RealmObject
import io.realm.kotlin.types.annotations.PrimaryKey
import org.mongodb.kbson.ObjectId

enum class ContactType {
    EMAIL, PHONE
}

class Contact(): RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var firstName: String = ""
    var lastName: String = ""
    var address: Address? = null
    var contactMethod: ContactMethod? = null
    var timestamp: RealmInstant = RealmInstant.now()

}

class Address(): RealmObject {
    @PrimaryKey
    var _id: ObjectId = ObjectId()
    var cityName: String = ""
    var timestamp: RealmInstant = RealmInstant.now()
}

class ContactMethod: RealmObject {
    var methodValue: String = ""
    var methodType: ContactType
        get() { return ContactType.valueOf(methodTypeDesc) }
        set(newMethodType) { methodTypeDesc = newMethodType.name }
    private var methodTypeDesc: String = ContactType.EMAIL.name
}
