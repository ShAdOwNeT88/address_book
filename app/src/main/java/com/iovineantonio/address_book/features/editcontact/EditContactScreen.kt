package com.iovineantonio.address_book.features.editcontact

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import arrow.core.Tuple5
import com.iovineantonio.address_book.R
import com.iovineantonio.address_book.databinding.ScreenEditContactBinding
import com.iovineantonio.address_book.features.addcontact.domain.Contact
import com.iovineantonio.address_book.features.addcontact.domain.ContactAuthenticator
import com.iovineantonio.address_book.utils.exhaustive
import com.iovineantonio.address_book.utils.visible
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import org.koin.android.ext.android.inject
import timber.log.Timber

class EditContactScreen : AppCompatActivity() {
    private lateinit var binding: ScreenEditContactBinding
    private val editContactViewModel: EditContactViewModel by inject()
    private var contactId: Int = 0
    private val disposables = CompositeDisposable()

    companion object {
        private const val CONTACT_ID: String = "CONTACT_ID"

        fun getIntent(context: Context, contactId: Int): Intent = Intent(context, EditContactScreen::class.java).apply {
            putExtra(CONTACT_ID, contactId)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupMonumentDetailsViewModel()
        binding = ScreenEditContactBinding.inflate(layoutInflater)

        contactId = intent.getIntExtra(CONTACT_ID, 0)
        editContactViewModel.send(EditContactEvent.RetrieveContactDetails(contactId = contactId))
        setToolbar()
        setInputObserver()
        setContentView(binding.root)
    }

    private fun setupMonumentDetailsViewModel() {
        editContactViewModel.observe(lifecycleScope) { state ->
            when (state) {
                is EditContactState.Error -> showError(state.error)
                is EditContactState.InProgress -> showProgress()
                is EditContactState.NoContactFound -> showError(Throwable("No contact could be retrieved with id: $contactId"))
                is EditContactState.RetrievedContact -> {
                    hideProgress()
                    setContactData(contact = state.contact)
                }

                is EditContactState.SaveDisabled -> binding.updateContactAction.isEnabled = false
                is EditContactState.SaveEnabled -> {
                    binding.updateContactAction.isEnabled = true
                    setUpdateAction(state.request)
                }

                is EditContactState.UpdatedContact -> {
                    hideProgress()
                    Toast.makeText(this, getString(R.string.edit_contact_successfully_updated), Toast.LENGTH_SHORT).show()
                    this.finish()
                }
            }.exhaustive
        }
    }

    private fun setContactData(contact: Contact) {
        binding.editContactName.setText(contact.name)
        binding.editContactSurname.setText(contact.surname)
        binding.editContactAddress.setText(contact.address)
        binding.editContactEmail.setText(contact.email)
        binding.editContactPhoneNumber.setText(contact.phoneNumber)
    }

    private fun setInputObserver() {
        Observable.combineLatest(
            binding.editContactName.textChanges(),
            binding.editContactSurname.textChanges(),
            binding.editContactAddress.textChanges(),
            binding.editContactEmail.textChanges(),
            binding.editContactPhoneNumber.textChanges()
        ) { name, surname, address, email, phoneNumber ->
            Tuple5(name.toString(), surname.toString(), address.toString(), email.toString(), phoneNumber.toString())
        }.subscribe(
            { (name, surname, address, email, phoneNumber) ->
                editContactViewModel.send(EditContactEvent.UpdateContactFormFilled(name, surname, address, email, phoneNumber))
            },
            { error -> showError(Throwable(error)) }
        ).addTo(disposables)
    }

    private fun setUpdateAction(request: ContactAuthenticator.AddContactRequest) {
        binding.updateContactAction.setOnClickListener {
            editContactViewModel.send(
                EditContactEvent.RequestToUpdateContact(
                    id = contactId,
                    name = request.name,
                    surname = request.surname,
                    address = request.address,
                    email = request.email.email,
                    phoneNumber = request.phoneNumber
                )
            )
        }
    }

    private fun setToolbar() {
        binding.toolbarTitle.text = resources.getString(R.string.edit_contact_title)
        binding.toolbarBack.setOnClickListener {
            this.finish()
        }
    }

    private fun showError(error: Throwable) {
        Timber.w(error, "[EditContactScreen] Error: ${error.message}")
        hideProgress()
        Toast.makeText(this, error.message, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        editContactViewModel.send(EditContactEvent.UnsubscribeFromEvents)
    }

    private fun showProgress() {
        binding.progressBar.visible(true)
    }

    private fun hideProgress() {
        binding.progressBar.visible(false)
    }
}
