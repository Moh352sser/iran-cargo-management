package com.irancargocompany.logistics.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val accessCode: String,
    val userType: UserType,
    val name: String?,
    val lastLogin: Long?,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) : Parcelable

enum class UserType {
    DRIVER,
    SUPERVISOR,
    MANAGER
}