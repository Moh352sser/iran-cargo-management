package com.irancargocompany.logistics.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.irancargocompany.logistics.model.LocationTracking

@Dao
interface LocationTrackingDao {
    
    @Query("SELECT * FROM location_tracking WHERE tripId = :tripId ORDER BY timestamp DESC")
    fun getLocationsByTrip(tripId: String): LiveData<List<LocationTracking>>
    
    @Query("SELECT * FROM location_tracking WHERE tripId = :tripId ORDER BY timestamp ASC")
    suspend fun getLocationsByTripSync(tripId: String): List<LocationTracking>
    
    @Query("SELECT * FROM location_tracking WHERE id = :locationId")
    suspend fun getLocationById(locationId: String): LocationTracking?
    
    @Query("SELECT * FROM location_tracking WHERE tripId = :tripId ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestLocationForTrip(tripId: String): LocationTracking?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocation(location: LocationTracking): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLocations(locations: List<LocationTracking>)
    
    @Delete
    suspend fun deleteLocation(location: LocationTracking)
    
    @Query("DELETE FROM location_tracking WHERE tripId = :tripId")
    suspend fun deleteLocationsByTrip(tripId: String)
    
    @Query("DELETE FROM location_tracking WHERE timestamp < :timestamp")
    suspend fun deleteOldLocations(timestamp: Long)
    
    @Query("SELECT COUNT(*) FROM location_tracking WHERE tripId = :tripId")
    suspend fun getLocationCountForTrip(tripId: String): Int
}