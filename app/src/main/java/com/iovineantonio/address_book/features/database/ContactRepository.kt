package com.iovineantonio.address_book.features.database

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class ContactRepository(private val todoDao: ContactDao) {

    fun getAllContacts(): Observable<List<ContactEntity>> = todoDao.getAllContacts()

    fun insert(contact: ContactEntity): Completable = todoDao.insert(contact)

    fun insertAll(contacts: List<ContactEntity>): Completable = todoDao.insertAll(*contacts.toTypedArray())

    fun delete(contact: ContactEntity): Completable = todoDao.delete(contact)

    fun update(contact: ContactEntity): Single<Int> {
        return todoDao.update(
            id = contact.id,
            name = contact.name,
            surname = contact.surname,
            phoneNumber = contact.phoneNumber,
            email = contact.email,
            address = contact.address
        )
    }

    fun findBySurname(surname: String): Observable<List<ContactEntity>> = todoDao.findBySurname(surname = surname)
}
