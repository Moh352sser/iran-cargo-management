package com.irancargocompany.logistics.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.irancargocompany.logistics.model.Trip
import com.irancargocompany.logistics.model.TripStatus

@Dao
interface TripDao {
    
    @Query("SELECT * FROM trips ORDER BY createdAt DESC")
    fun getAllTrips(): LiveData<List<Trip>>
    
    @Query("SELECT * FROM trips WHERE status = :status ORDER BY createdAt DESC")
    fun getTripsByStatus(status: TripStatus): LiveData<List<Trip>>
    
    @Query("SELECT * FROM trips WHERE driverId = :driverId ORDER BY createdAt DESC")
    fun getTripsByDriver(driverId: String): LiveData<List<Trip>>
    
    @Query("SELECT * FROM trips WHERE supervisorId = :supervisorId ORDER BY createdAt DESC")
    fun getTripsBySupervisor(supervisorId: String): LiveData<List<Trip>>
    
    @Query("SELECT * FROM trips WHERE id = :tripId")
    suspend fun getTripById(tripId: String): Trip?
    
    @Query("SELECT * FROM trips WHERE qrCode = :qrCode")
    suspend fun getTripByQrCode(qrCode: String): Trip?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrip(trip: Trip): Long
    
    @Update
    suspend fun updateTrip(trip: Trip)
    
    @Delete
    suspend fun deleteTrip(trip: Trip)
    
    @Query("UPDATE trips SET status = :status, updatedAt = :updatedAt WHERE id = :tripId")
    suspend fun updateTripStatus(tripId: String, status: TripStatus, updatedAt: Long = System.currentTimeMillis())
    
    @Query("SELECT COUNT(*) FROM trips WHERE status = :status")
    suspend fun getTripCountByStatus(status: TripStatus): Int
    
    @Query("SELECT COUNT(*) FROM trips WHERE driverId = :driverId AND status = :status")
    suspend fun getTripCountByDriverAndStatus(driverId: String, status: TripStatus): Int
}