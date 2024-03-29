package com.iovineantonio.address_book.features

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.core.content.ContextCompat.startActivity
import com.iovineantonio.address_book.features.main.MainScreen


interface Navigator {
    fun openWebBrowser(activity: Activity, url: String)
    fun startActivityWithIntent(activity: Activity, intent: Intent)
    fun openHomeScreen(activity: Activity)
    fun openBrowser(activity: Activity, url: String)
    fun openEmail(activity: Activity, emailAddress: String)
    fun openDialer(activity: Activity, phoneNumber: String)
}

class AppNavigator : Navigator {
    override fun openWebBrowser(activity: Activity, url: String) {
        val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivityWithIntent(activity, browserIntent)
    }

    override fun startActivityWithIntent(activity: Activity, intent: Intent) {
        activity.startActivity(intent)
    }

    override fun openHomeScreen(activity: Activity) {
        startActivityWithIntent(activity, MainScreen.getIntent(context = activity))
    }

    override fun openBrowser(activity: Activity, url: String) {
        startActivityWithIntent(activity, Intent(Intent.ACTION_VIEW).setData((Uri.parse(url))))
    }

    override fun openEmail(activity: Activity, emailAddress: String) {
        val intent = Intent(Intent.ACTION_SENDTO)
        intent.setData(Uri.parse("mailto:"))
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddress)
        startActivityWithIntent(activity, intent)
    }

    override fun openDialer(activity: Activity, phoneNumber: String) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.setData(Uri.parse("tel:".plus(phoneNumber)))
        startActivityWithIntent(activity, intent)
    }
}
