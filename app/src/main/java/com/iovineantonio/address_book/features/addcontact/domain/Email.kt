package com.iovineantonio.address_book.features.addcontact.domain

import arrow.core.Either
import arrow.core.Option
import arrow.core.left
import arrow.core.right
import arrow.core.toOption

object EmailTypeError : Exception("Email is not well formed")

class Email private constructor(val email: String) {
    companion object {
        private const val EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\$"

        /**
         * Method used to create a valid String Object
         * @param email String that contains an email address to check
         * @return Option with Email Object or empty if the email address is not well formed
         * */
        fun from(email: String): Either<EmailTypeError, Email> {
            val validStreet = email.isNotEmpty() && email.matches(EMAIL_REGEX.toRegex())
            return if (validStreet) {
                Email(email).right()
            } else EmailTypeError.left()
        }
    }
}
