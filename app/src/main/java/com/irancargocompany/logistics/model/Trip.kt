package com.irancargocompany.logistics.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "trips")
data class Trip(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val origin: String,
    val destination: String,
    val cargoType: String,
    val cargoWeight: Double,
    val driverName: String,
    val vehicleNumber: String,
    val departureTime: Long,
    val arrivalTime: Long?,
    val status: TripStatus,
    val driverId: String,
    val supervisorId: String?,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val qrCode: String?,
    val notes: String?
) : Parcelable

enum class TripStatus {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED
}