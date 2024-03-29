@file:SuppressWarnings("TooManyFunctions")

package com.iovineantonio.address_book.utils

import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import org.threeten.bp.DayOfWeek
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import org.threeten.bp.Month
import org.threeten.bp.OffsetDateTime
import org.threeten.bp.ZoneId
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.DateTimeFormatter
import org.threeten.bp.format.FormatStyle
import org.threeten.bp.format.TextStyle

private val userTimeZone: ZoneId = ZoneId.systemDefault()

fun getFormattedDate(dateToFormat: OffsetDateTime): String =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM).withZone(userTimeZone).format(dateToFormat)

//TODO We need to get format output for formatted date such as (Wed, May 26th)
fun getFormattedFullDate(dateToFormat: OffsetDateTime): String =
    DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withZone(userTimeZone).format(dateToFormat)

fun getFormattedTime(dateToFormat: OffsetDateTime): String = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withZone(userTimeZone).format(dateToFormat)

private fun getCurrencyAmountFormatter(locale: Locale, currency: String): NumberFormat {
    return NumberFormat.getCurrencyInstance(locale).let {
        it.currency = Currency.getInstance(currency)
        it
    }
}

fun getFormattedAmount(amount: Double, currencyCode: String, locale: Locale): String =
    getCurrencyAmountFormatter(locale, currencyCode).format(amount)

fun localizeMonth(month: Int, locale: Locale): String = Month.of(month).getDisplayName(TextStyle.FULL_STANDALONE, locale)

fun localizeDay(dayOfWeek: Int, locale: Locale): String =
    DayOfWeek.of(dayOfWeek).getDisplayName(TextStyle.FULL_STANDALONE, locale).toLowerCase(Locale.getDefault()).capitalize(Locale.getDefault())

fun checkIfDateIsToday(date: OffsetDateTime): Boolean =
    date.dayOfMonth == OffsetDateTime.now().dayOfMonth && date.month == OffsetDateTime.now().month && date.year == OffsetDateTime.now().year

/**
 * @param date String that identify a date formatted as dddddddd dd mmmmmm yyyy (domenica 25 giugno 2023)
 * @return date formatted as standard OffsetDateTime in String object
 * Convert date formatted with dddddddd dd mmmmmm yyyy (domenica 25 giugno 2023) into an OffsetDateTime standard date
 */
fun obtainValidDate(date: String): String {
    val dateSplit = date.split(" ")
    val dayNum = dateSplit[1].toInt()
    val month = getMonthFromLiteral(dateSplit[2]) ?: 1
    val year = dateSplit.last().toInt()

    val localDate = LocalDate.of(year, month, dayNum)

    return OffsetDateTime.of(localDate, LocalTime.MIDNIGHT, ZoneOffset.UTC).toString()
}

/**
 * @param month String that identify a month literal
 * @return month as Integer value
 * Pass string that identify month and get back the appropriate Int value
 */
private fun getMonthFromLiteral(month: String): Int? {
    return when (month.lowercase()) {
        "gennaio" -> 1
        "febbraio" -> 2
        "marzo" -> 3
        "aprile" -> 4
        "maggio" -> 5
        "giugno" -> 6
        "luglio" -> 7
        "agosto" -> 8
        "settembre" -> 9
        "ottobre" -> 10
        "novembre" -> 11
        "dicembre" -> 12
        else -> null
    }
}
