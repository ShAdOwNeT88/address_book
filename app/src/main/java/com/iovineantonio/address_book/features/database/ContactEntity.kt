package com.iovineantonio.address_book.features.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.iovineantonio.address_book.features.addcontact.domain.Contact

@Entity(tableName = "contacts")
data class ContactEntity(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "surname") val surname: String?,
    @ColumnInfo(name = "phoneNumber") val phoneNumber: String?,
    @ColumnInfo(name = "email") val email: String?,
    @ColumnInfo(name = "address") val address: String?,
)

