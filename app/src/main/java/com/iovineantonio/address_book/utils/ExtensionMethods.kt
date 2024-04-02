@file:SuppressWarnings("TooManyFunctions")

package com.iovineantonio.address_book.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.app.NavUtils
import androidx.core.app.TaskStackBuilder
import com.iovineantonio.address_book.R
import timber.log.Timber

val <T> T.exhaustive: T
    get() = this

fun View.visible(visible: Boolean) {
    visibility = if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

fun View.actionNotImplementedListener(context: Context, actionName: String) {
    this.setOnClickListener {
        Timber.w("%s action requested - not implemented!", actionName)
        Toast.makeText(context, context.getString(R.string.action_not_implemented), Toast.LENGTH_SHORT).show()
    }
}

private fun buildActivityStack(activity: Activity) {
    val upIntent: Intent? = NavUtils.getParentActivityIntent(activity)
    if (upIntent == null) {
        Timber.e(IllegalStateException("UpIntent is null"), "cannot buildActivityStack()")
    } else {
        TaskStackBuilder.create(activity)
            .addNextIntentWithParentStack(upIntent)
            .startActivities()
    }
}

fun Activity.goToParentActivity() {
    val upIntent = NavUtils.getParentActivityIntent(this)

    if (upIntent != null) {
        if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
            buildActivityStack(this)
        } else {
            navigateUpTo(upIntent)
        }
    } else {
        finish()
    }
}

fun Context.showKeyboard() {
    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
}

fun Context.hideKeyboard() {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    //Find the currently focused view, so we can grab the correct window token from it.
    var view = (this as Activity).currentFocus
    //If no view currently has focus, create a new one, just so we can grab a window token from it
    if (view == null) {
        view = View(this)
    }
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}

fun createUnderlinedString(stringToUnderline: String): SpannableString {
    val spannableString = SpannableString(stringToUnderline)
    spannableString.setSpan(UnderlineSpan(), 0, spannableString.length, 0)
    return spannableString
}

