package com.iovineantonio.address_book.features.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

@Dao
interface ContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(contact: ContactEntity): Completable

    @Delete
    fun delete(contact: ContactEntity): Completable

    @Query("SELECT * from contacts order by id ASC")
    fun getAllContacts(): Observable<List<ContactEntity>>

    @Query("UPDATE contacts set name = :name, surname = :surname, phoneNumber = :phoneNumber, email = :email, address = :address where id = :id")
    fun update(id: Int?, name: String?, surname: String?, phoneNumber: String?, email: String?, address: String?): Single<Int>
}
