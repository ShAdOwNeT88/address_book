package com.iovineantonio.address_book.features.addcontact.domain

import com.iovineantonio.address_book.features.database.ContactEntity

sealed class Contact(
    val name: String,
    val surname: String,
    val phoneNumber: String,
    val email: String,
    val address: String,
) {
    class ContactWithId(
        val id: Int,
        name: String,
        surname: String,
        phoneNumber: String,
        email: String,
        address: String,
    ) : Contact(name = name, surname = surname, phoneNumber = phoneNumber, email = email, address = address)

    class ContactWithoutId(
        name: String,
        surname: String,
        phoneNumber: String,
        email: String,
        address: String,
    ) : Contact(name = name, surname = surname, phoneNumber = phoneNumber, email = email, address = address)

    override fun toString(): String {
        return "Contact(name='$name', surname='$surname', phoneNumber='$phoneNumber', email='$email', address='$address')"
    }
}

fun Contact.ContactWithId.toEntity(): ContactEntity {
    return ContactEntity(
        id = id,
        name = name,
        surname = surname,
        phoneNumber = phoneNumber,
        email = email,
        address = address
    )
}

fun ContactEntity.toDomain(): Contact.ContactWithId {
    return Contact.ContactWithId(
        id = this.id ?: 0,
        name = this.name ?: "",
        surname = this.surname ?: "",
        phoneNumber = this.phoneNumber ?: "",
        email = this.email ?: "",
        address = this.address ?: "",
    )
}
