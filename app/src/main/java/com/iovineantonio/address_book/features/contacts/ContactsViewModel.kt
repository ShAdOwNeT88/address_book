package com.iovineantonio.address_book.features.contacts

import com.iovineantonio.address_book.base.BaseViewModel
import com.iovineantonio.address_book.features.addcontact.domain.Contact
import com.iovineantonio.address_book.features.addcontact.domain.toDomain
import com.iovineantonio.address_book.features.addcontact.domain.toEntity
import com.iovineantonio.address_book.features.database.ContactRepository
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.Disposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers
import timber.log.Timber


sealed class ContactsEvent {
    object UnsubscribeFromEvents : ContactsEvent()
    object RetrieveAllContacts : ContactsEvent()
    data class RequestToDeleteContact(val contact: Contact.ContactWithId) : ContactsEvent()
}

sealed class ContactsState {
    data class RetrievedContacts(val contacts: List<Contact>) : ContactsState()
    object RetrievedEmptyContactList : ContactsState()
    object DeletedContact : ContactsState()
    data class Error(val error: Throwable) : ContactsState()
    object InProgress : ContactsState()
}

class ContactsViewModel(private val scheduler: Scheduler, private val contactRepository: ContactRepository) : BaseViewModel<ContactsState, ContactsEvent>() {

    private var getAllContactsSubscription = Disposable.disposed()
    private var deleteContactSubscription = Disposable.disposed()

    override fun send(event: ContactsEvent) {
        when (event) {
            is ContactsEvent.UnsubscribeFromEvents -> unsubscribeFromEvents()
            is ContactsEvent.RetrieveAllContacts -> getAllContacts()
            is ContactsEvent.RequestToDeleteContact -> deleteContact(contact = event.contact)
        }
    }

    private fun getAllContacts() {
        if (getAllContactsSubscription.isDisposed) {
            post(ContactsState.InProgress)
            getAllContactsSubscription = contactRepository.getAllContacts()
                .observeOn(scheduler)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { list ->
                        if (list.isEmpty()) {
                            post(ContactsState.RetrievedEmptyContactList)
                        } else {
                            post(ContactsState.RetrievedContacts(list.map { it.toDomain() }))
                        }
                    },
                    { error -> Timber.e("Response completed with error $error") }
                ).addTo(disposables)
        }
    }

    private fun deleteContact(contact: Contact.ContactWithId) {
        if (deleteContactSubscription.isDisposed) {
            post(ContactsState.InProgress)
            deleteContactSubscription = contactRepository.delete(contact.toEntity())
                .observeOn(scheduler)
                .subscribeOn(Schedulers.io())
                .subscribe(
                    { post(ContactsState.DeletedContact) },
                    { error -> post(ContactsState.Error(error)) }
                ).addTo(disposables)
        }
    }

    private fun unsubscribeFromEvents() {
        disposables.dispose()
    }

}
