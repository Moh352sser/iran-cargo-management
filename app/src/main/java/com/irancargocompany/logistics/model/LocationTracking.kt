package com.irancargocompany.logistics.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "location_tracking")
data class LocationTracking(
    @PrimaryKey
    val id: String = UUID.randomUUID().toString(),
    val tripId: String,
    val latitude: Double,
    val longitude: Double,
    val accuracy: Float,
    val speed: Float?,
    val bearing: Float?,
    val timestamp: Long = System.currentTimeMillis(),
    val address: String?
)