package com.iovineantonio.address_book.features.addcontact.domain

import arrow.core.Either
import arrow.core.left
import arrow.core.right

class ContactAuthenticator {

    data class ContactError(val message: String = "Error during contact creation")
    data class AddContactRequest(
        val name: String,
        val surname: String,
        val address: String,
        val email: Email,
        val phoneNumber: String,
    ) {
        companion object {
            fun from(name: String, surname: String, address: String, email: String, phoneNumber: String): Either<ContactError, AddContactRequest> {
                Email.from(email).fold(
                    { return ContactError().left() },
                    { return AddContactRequest(name = name, surname = surname, address = address, email = it, phoneNumber = phoneNumber).right() }
                )
            }
        }
    }
}
