package com.iovineantonio.address_book.features.contacts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.iovineantonio.address_book.R
import com.iovineantonio.address_book.databinding.ScreenContactsBinding
import com.iovineantonio.address_book.features.Navigator
import com.iovineantonio.address_book.features.addcontact.domain.Contact
import com.iovineantonio.address_book.features.contacts.adapters.ContactListener
import com.iovineantonio.address_book.features.contacts.adapters.ContactsAdapter
import com.iovineantonio.address_book.utils.exhaustive
import com.iovineantonio.address_book.utils.visible
import org.koin.android.ext.android.inject
import timber.log.Timber

class ContactsScreen : Fragment() {
    private lateinit var binding: ScreenContactsBinding
    private lateinit var contactsAdapter: ContactsAdapter
    private val contactsViewModel: ContactsViewModel by inject()
    private val navigator: Navigator by inject()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = ScreenContactsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        setupContactsViewModel()
        setContactsList()
        setSearch()
        contactsViewModel.send(ContactsEvent.RetrieveAllContacts)
        return root
    }

    private fun setupContactsViewModel() {
        contactsViewModel.observe(lifecycleScope) { state ->
            when (state) {
                is ContactsState.InProgress -> showProgress()
                is ContactsState.Error -> showError(state.error)
                is ContactsState.RetrievedContacts -> showContactsSection(state.contacts)
                is ContactsState.RetrievedEmptyContactList -> showEmptyContactsSection()
                is ContactsState.DeletedContact -> {
                    hideProgress()
                    contactsViewModel.send(ContactsEvent.RetrieveAllContacts)
                }
            }.exhaustive
        }
    }

    private fun setContactsList() {
        contactsAdapter = ContactsAdapter(
            context = requireContext(),
            contactsListener = object : ContactListener {
                override fun deleteContact(contact: Contact.ContactWithId) {
                    contactsViewModel.send(ContactsEvent.RequestToDeleteContact(contact = contact))
                }

                override fun callContact(contact: Contact.ContactWithId) {
                    navigator.openDialer(requireActivity(), contact.phoneNumber)
                }

                override fun emailContact(contact: Contact.ContactWithId) {
                    navigator.openEmail(requireActivity(), contact.email)
                }
            }
        )

        binding.contactsList.adapter = contactsAdapter
    }

    private fun showEmptyContactsSection() {
        hideProgress()
        binding.contactsList.visible(false)
        binding.contactsEmptyList.visible(true)

    }

    private fun showContactsSection(contacts: List<Contact.ContactWithId>) {
        hideProgress()
        binding.contactsList.visible(true)
        binding.contactsEmptyList.visible(false)
        contactsAdapter.submitList(contacts)
    }

    private fun setSearch() {
        binding.contactsSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (binding.contactsSearch.text?.isBlank() == true) {
                    contactsViewModel.send(ContactsEvent.RetrieveAllContacts)
                } else {
                    val surnameToSearch = binding.contactsSearch.text.toString()
                    contactsViewModel.send(ContactsEvent.RequestToFindContactsBySurname(surnameToSearch))
                }
            }
            return@setOnEditorActionListener true
        }
    }

    private fun showError(error: Throwable) {
        hideProgress()
        Timber.e("[ContactsScreen] Error: $error")
        Toast.makeText(requireContext(), getString(R.string.generic_error), Toast.LENGTH_LONG).show()
    }

    private fun showProgress() {
        binding.progressBar.visible(true)
    }

    private fun hideProgress() {
        binding.progressBar.visible(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        contactsViewModel.send(ContactsEvent.UnsubscribeFromEvents)
    }
}
