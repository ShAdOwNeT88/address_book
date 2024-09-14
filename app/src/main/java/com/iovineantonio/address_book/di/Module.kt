package com.iovineantonio.address_book.di

import com.iovineantonio.address_book.features.AppNavigator
import com.iovineantonio.address_book.features.Navigator
import com.iovineantonio.address_book.features.about.AboutViewModel
import com.iovineantonio.address_book.features.addcontact.AddContactViewModel
import com.iovineantonio.address_book.features.contacts.ContactsViewModel
import com.iovineantonio.address_book.features.database.ContactDatabase
import com.iovineantonio.address_book.features.database.ContactRepository
import com.iovineantonio.address_book.features.editcontact.EditContactViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

typealias DebugFlag = Boolean

val viewModels = module {
    viewModel { ContactsViewModel(scheduler = AndroidSchedulers.mainThread(), contactRepository = get()) }
    viewModel { AddContactViewModel(scheduler = AndroidSchedulers.mainThread(), contactRepository = get()) }
    viewModel { EditContactViewModel(scheduler = AndroidSchedulers.mainThread(), contactRepository = get()) }
    viewModel { AboutViewModel(scheduler = AndroidSchedulers.mainThread(), contactRepository = get()) }
}

val androidComponents = module {
    single { androidContext().resources }
    single { androidContext().packageManager }
    single { androidContext().contentResolver }
}

val appComponents = module {
    single<Navigator> { AppNavigator() }
}

val databaseModule = module {
    single { ContactDatabase.getDatabase(androidApplication()) }
    single { ContactDatabase.getDatabase(get()).getContactDao() }
    single { ContactRepository(get()) }
}
