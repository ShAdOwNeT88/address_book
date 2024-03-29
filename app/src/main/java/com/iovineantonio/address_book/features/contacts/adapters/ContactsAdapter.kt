package com.iovineantonio.address_book.features.contacts.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.iovineantonio.address_book.databinding.ContactDetailsItemBinding
import com.iovineantonio.address_book.features.addcontact.domain.Contact

class ContactsAdapter(val context: Context, private val contactsListener: ContactListener) : ListAdapter<Contact, ContactsViewHolder>(NewsDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsViewHolder {
        val binding = ContactDetailsItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactsViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactsViewHolder, listPosition: Int) {
        val news = getItem(listPosition)
        holder.bind(news, contactsListener)
    }
}

class NewsDiffCallback : DiffUtil.ItemCallback<Contact>() {
    override fun areItemsTheSame(oldItem: Contact, newItem: Contact): Boolean = oldItem.email == newItem.email
    override fun areContentsTheSame(oldItem: Contact, newItem: Contact): Boolean = (oldItem.name == newItem.name)
            && (oldItem.surname == newItem.surname)
            && (oldItem.phoneNumber == newItem.phoneNumber)
            && (oldItem.email == newItem.email)
            && (oldItem.address == newItem.address)
}

class ContactsViewHolder(private var binding: ContactDetailsItemBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(contact: Contact, contactListener: ContactListener) {
        binding.contactDetailsNameSurname.text = contact.name.plus(" ").plus(contact.surname)
        binding.contactDetailsPhone.text = contact.phoneNumber
        binding.contactDetailsEmail.text = contact.email
        binding.contactDetailsAddress.text = contact.address

        binding.contactDetailsDeleteAction.setOnClickListener {
            contactListener.deleteContact(contact)
        }

        binding.contactDetailsPhone.setOnClickListener {
            contactListener.callContact(contact)
        }

        binding.contactDetailsEmail.setOnClickListener {
            contactListener.emailContact(contact)
        }
    }
}

interface ContactListener {
    fun deleteContact(contact: Contact)
    fun callContact(contact: Contact)
    fun emailContact(contact: Contact)
}
