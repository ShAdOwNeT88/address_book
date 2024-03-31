package com.iovineantonio.address_book.features.addcontact

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import arrow.core.Tuple5
import com.iovineantonio.address_book.R
import com.iovineantonio.address_book.databinding.ScreenAddContactBinding
import com.iovineantonio.address_book.features.addcontact.domain.ContactAuthenticator
import com.iovineantonio.address_book.utils.exhaustive
import com.iovineantonio.address_book.utils.visible
import com.jakewharton.rxbinding4.widget.textChanges
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.kotlin.addTo
import org.koin.android.ext.android.inject
import timber.log.Timber

class AddContactScreen : Fragment() {
    private lateinit var binding: ScreenAddContactBinding
    private val addContactViewModel: AddContactViewModel by inject()
    private val disposables = CompositeDisposable()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ScreenAddContactBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupAddContactViewModel()
        setupInputObserver()
        setActions()
        return root
    }

    private fun setupAddContactViewModel() {
        addContactViewModel.observe(lifecycleScope) { state ->
            when (state) {
                is AddContactState.Error -> showError(state.error)
                is AddContactState.InProgress -> showProgress()
                is AddContactState.SaveEnabled -> {
                    setSaveAction(state.request)
                    binding.saveContactAction.isEnabled = true
                }

                is AddContactState.SaveDisabled -> binding.saveContactAction.isEnabled = false
                is AddContactState.SaveCompleted -> {
                    hideProgress()
                    clearFields()
                    showCompleted()
                }
            }.exhaustive
        }
    }

    private fun setupInputObserver() {
        Observable.combineLatest(
            binding.addContactName.textChanges(),
            binding.addContactSurname.textChanges(),
            binding.addContactAddress.textChanges(),
            binding.addContactEmail.textChanges(),
            binding.addContactPhoneNumber.textChanges()
        ) { name, surname, address, email, phoneNumber ->
            Tuple5(name.toString(), surname.toString(), address.toString(), email.toString(), phoneNumber.toString())
        }.subscribe(
            { (name, surname, address, email, phoneNumber) ->
                addContactViewModel.send(AddContactEvent.RegisterContactFormFilled(name, surname, address, email, phoneNumber))
            },
            { error -> showError(Throwable(error)) }
        ).addTo(disposables)
    }

    private fun setSaveAction(request: ContactAuthenticator.AddContactRequest) {
        binding.saveContactAction.setOnClickListener {
            addContactViewModel.send(
                AddContactEvent.RequestToSaveContact(
                    name = request.name,
                    surname = request.surname,
                    address = request.address,
                    email = request.email.email,
                    phoneNumber = request.phoneNumber
                )
            )
        }
    }

    private fun setActions() {
        binding.saveContactAction.isEnabled = false
        binding.addMockContactsAction.setOnClickListener { addContactViewModel.send(AddContactEvent.RequestToSaveMockContacts) }
    }

    private fun showError(error: Throwable) {
        hideProgress()
        Timber.e("Error in Add Contact section: $error")
        Toast.makeText(requireContext(), getString(R.string.generic_error), Toast.LENGTH_LONG).show()
    }

    private fun clearFields() {
        binding.addContactName.text.clear()
        binding.addContactSurname.text.clear()
        binding.addContactAddress.text.clear()
        binding.addContactEmail.text.clear()
        binding.addContactPhoneNumber.text.clear()
    }

    private fun showCompleted() {
        Toast.makeText(requireContext(), resources.getString(R.string.save_completed), Toast.LENGTH_LONG).show()
    }

    private fun showProgress() {
        binding.progressBar.visible(true)
    }

    private fun hideProgress() {
        binding.progressBar.visible(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        addContactViewModel.send(AddContactEvent.UnsubscribeFromEvents)
    }
}
