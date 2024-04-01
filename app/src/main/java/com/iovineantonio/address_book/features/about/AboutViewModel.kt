package com.iovineantonio.address_book.features.about

import com.iovineantonio.address_book.base.BaseViewModel
import com.iovineantonio.address_book.features.database.ContactRepository
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import io.reactivex.rxjava3.schedulers.Schedulers


sealed class AboutEvent {
    object UnsubscribeFromEvents : AboutEvent()
    object RequestToDeleteAllContacts : AboutEvent()
}

sealed class AboutState {
    object DeletedAllContacts : AboutState()
    data class Error(val error: Throwable) : AboutState()
    object InProgress : AboutState()
}

class AboutViewModel(private val scheduler: Scheduler, private val contactRepository: ContactRepository) : BaseViewModel<AboutState, AboutEvent>() {
    private val compositeDisposable = CompositeDisposable()

    override fun send(event: AboutEvent) {
        when (event) {
            is AboutEvent.UnsubscribeFromEvents -> unsubscribeFromEvents()
            is AboutEvent.RequestToDeleteAllContacts -> deleteAllContacts()
        }
    }

    private fun deleteAllContacts() {
        post(AboutState.InProgress)
        contactRepository.deleteAllContacts()
            .observeOn(scheduler)
            .subscribeOn(Schedulers.io())
            .subscribe(
                { post(AboutState.DeletedAllContacts) },
                { error -> post(AboutState.Error(Throwable("Error while delete all contacts $error"))) }
            ).addTo(compositeDisposable)

    }

    private fun unsubscribeFromEvents() {
        compositeDisposable.clear()
    }

}
