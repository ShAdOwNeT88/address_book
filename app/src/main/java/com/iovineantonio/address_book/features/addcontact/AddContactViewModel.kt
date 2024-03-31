package com.iovineantonio.address_book.features.addcontact

import com.iovineantonio.address_book.base.BaseViewModel
import com.iovineantonio.address_book.features.addcontact.domain.Contact
import com.iovineantonio.address_book.features.addcontact.domain.ContactAuthenticator
import com.iovineantonio.address_book.features.addcontact.domain.toEntity
import com.iovineantonio.address_book.features.database.ContactEntity
import com.iovineantonio.address_book.features.database.ContactRepository
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers

sealed class AddContactEvent {
    object UnsubscribeFromEvents : AddContactEvent()
    data class RegisterContactFormFilled(
        val name: String,
        val surname: String,
        val address: String,
        val email: String,
        val phoneNumber: String,
    ) : AddContactEvent()

    data class RequestToSaveContact(val name: String, val surname: String, val address: String, val email: String, val phoneNumber: String) : AddContactEvent()
    object RequestToSaveMockContacts : AddContactEvent()
}

sealed class AddContactState {
    data class SaveEnabled(val request: ContactAuthenticator.AddContactRequest) : AddContactState()
    object SaveDisabled : AddContactState()
    object SaveCompleted : AddContactState()
    data class Error(val error: Throwable) : AddContactState()
    object InProgress : AddContactState()
}

class AddContactViewModel(private val scheduler: Scheduler, private val contactRepository: ContactRepository) :
    BaseViewModel<AddContactState, AddContactEvent>() {
    private val compositeDisposable = CompositeDisposable()

    override fun send(event: AddContactEvent) {
        when (event) {
            is AddContactEvent.UnsubscribeFromEvents -> unsubscribeFromEvents()
            is AddContactEvent.RegisterContactFormFilled -> {
                ContactAuthenticator.AddContactRequest
                    .from(name = event.name, surname = event.surname, phoneNumber = event.phoneNumber, email = event.email, address = event.address)
                    .fold(
                        { error -> post(AddContactState.SaveDisabled) },
                        { request -> post(AddContactState.SaveEnabled(request)) }
                    )
            }

            is AddContactEvent.RequestToSaveContact -> {
                val contact = Contact.ContactWithoutId(
                    name = event.name,
                    surname = event.surname,
                    phoneNumber = event.phoneNumber,
                    email = event.email,
                    address = event.address
                )
                saveContact(contact)
            }

            is AddContactEvent.RequestToSaveMockContacts -> saveContacts(
                listOf(
                    Contact.ContactWithoutId(
                        name = "Mario",
                        surname = "Rossi",
                        phoneNumber = "3333333331",
                        email = "mario.rossi@email.com",
                        address = "Via Rossi 1, Roma"
                    ),
                    Contact.ContactWithoutId(
                        name = "Giuseppe",
                        surname = "Verdi",
                        phoneNumber = "3333333332",
                        email = "giuseppe.verdi@email.com",
                        address = "Via Verdi 2, Foligno"
                    ),
                    Contact.ContactWithoutId(
                        name = "Antonio",
                        surname = "Passalacqua",
                        phoneNumber = "3333333333",
                        email = "antonio.passa@email.com",
                        address = "Via Rossi 1, Firenze"
                    ),
                    Contact.ContactWithoutId(
                        name = "Italo",
                        surname = "Bianchi",
                        phoneNumber = "3333333334",
                        email = "italo.bianchi@email.com",
                        address = "Via Rossi 1, Urbino"
                    ),
                )
            )
        }
    }

    private fun saveContact(contact: Contact) {
        post(AddContactState.InProgress)
        contactRepository.insert(
            ContactEntity(
                id = null,
                name = contact.name,
                surname = contact.surname,
                phoneNumber = contact.phoneNumber,
                email = contact.email,
                address = contact.address
            )
        )
            .observeOn(scheduler)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { post(AddContactState.SaveCompleted) },
                { error -> post(AddContactState.Error(error)) }
            ).addTo(compositeDisposable)
    }

    private fun saveContacts(contacts: List<Contact>) {
        post(AddContactState.InProgress)
        contactRepository.insertAll(contacts.map { (it as Contact.ContactWithoutId).toEntity() })
            .observeOn(scheduler)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { post(AddContactState.SaveCompleted) },
                { error -> post(AddContactState.Error(error)) }
            ).addTo(compositeDisposable)
    }

    private fun unsubscribeFromEvents() {
        compositeDisposable.clear()
    }
}
