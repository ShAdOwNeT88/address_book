package com.iovineantonio.address_book.features.editcontact

import com.iovineantonio.address_book.base.BaseViewModel
import com.iovineantonio.address_book.features.addcontact.domain.Contact
import com.iovineantonio.address_book.features.addcontact.domain.ContactAuthenticator
import com.iovineantonio.address_book.features.addcontact.domain.toDomain
import com.iovineantonio.address_book.features.addcontact.domain.toEntity
import com.iovineantonio.address_book.features.database.ContactRepository
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers


sealed class EditContactEvent {
    object UnsubscribeFromEvents : EditContactEvent()
    data class RetrieveContactDetails(val contactId: Int) : EditContactEvent()
    data class UpdateContactFormFilled(
        val name: String,
        val surname: String,
        val address: String,
        val email: String,
        val phoneNumber: String,
    ) : EditContactEvent()

    data class RequestToUpdateContact(
        val id: Int,
        val name: String,
        val surname: String,
        val address: String,
        val email: String,
        val phoneNumber: String,
    ) : EditContactEvent()

}

sealed class EditContactState {
    data class RetrievedContact(val contact: Contact) : EditContactState()
    object NoContactFound : EditContactState()
    data class SaveEnabled(val request: ContactAuthenticator.AddContactRequest) : EditContactState()
    object SaveDisabled : EditContactState()
    object UpdatedContact : EditContactState()
    data class Error(val error: Throwable) : EditContactState()
    object InProgress : EditContactState()
}

class EditContactViewModel(private val scheduler: Scheduler, private val contactRepository: ContactRepository) :
    BaseViewModel<EditContactState, EditContactEvent>() {

    private val compositeDisposable = CompositeDisposable()

    override fun send(event: EditContactEvent) {
        when (event) {
            is EditContactEvent.RetrieveContactDetails -> searchContactById(contactId = event.contactId)
            is EditContactEvent.UnsubscribeFromEvents -> unsubscribeFromEvents()
            is EditContactEvent.UpdateContactFormFilled -> {
                ContactAuthenticator.AddContactRequest
                    .from(name = event.name, surname = event.surname, phoneNumber = event.phoneNumber, email = event.email, address = event.address)
                    .fold(
                        { error -> post(EditContactState.SaveDisabled) },
                        { request -> post(EditContactState.SaveEnabled(request)) }
                    )
            }

            is EditContactEvent.RequestToUpdateContact -> updateContact(
                Contact.ContactWithId(
                    id = event.id,
                    name = event.name,
                    surname = event.surname,
                    phoneNumber = event.phoneNumber,
                    email = event.email,
                    address = event.address
                )
            )
        }
    }

    private fun searchContactById(contactId: Int) {
        post(EditContactState.InProgress)
        contactRepository.findById(contactId = contactId)
            .observeOn(scheduler)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { contact -> post(EditContactState.RetrievedContact(contact = contact.toDomain())) },
                { error -> post(EditContactState.Error(Throwable("Error during search with id with error: $error"))) }
            ).addTo(disposables)
    }

    private fun updateContact(contact: Contact.ContactWithId) {
        post(EditContactState.InProgress)
        contactRepository.update(contact = contact.toEntity())
            .observeOn(scheduler)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { result -> post(EditContactState.UpdatedContact) },
                { error -> post(EditContactState.Error(Throwable("Error during search with id with error: $error"))) }
            ).addTo(disposables)
    }

    private fun unsubscribeFromEvents() {
        compositeDisposable.clear()
    }

}
