package com.iovineantonio.address_book.features.database

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class ContactRepository(private val contactDao: ContactDao) {

    fun getAllContacts(): Observable<List<ContactEntity>> = contactDao.getAllContacts()
    fun findById(contactId: Int): Observable<ContactEntity> = contactDao.getContactById(id = contactId)
    fun insert(contact: ContactEntity): Completable = contactDao.insert(contact)
    fun insertAll(contacts: List<ContactEntity>): Completable = contactDao.insertAll(*contacts.toTypedArray())
    fun delete(contact: ContactEntity): Completable = contactDao.delete(contact)
    fun deleteAllContacts(): Completable = contactDao.deleteAllContacts()
    fun update(contact: ContactEntity): Single<Int> {
        return contactDao.update(
            id = contact.id,
            name = contact.name,
            surname = contact.surname,
            phoneNumber = contact.phoneNumber,
            email = contact.email,
            address = contact.address
        )
    }

    fun findBySurname(surname: String): Observable<List<ContactEntity>> = contactDao.findBySurname(surname = surname)
}
