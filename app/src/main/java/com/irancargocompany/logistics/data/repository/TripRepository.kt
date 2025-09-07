package com.irancargocompany.logistics.data.repository

import androidx.lifecycle.LiveData
import com.irancargocompany.logistics.data.database.TripDao
import com.irancargocompany.logistics.model.Trip
import com.irancargocompany.logistics.model.TripStatus

class TripRepository(private val tripDao: TripDao) {
    
    fun getAllTrips(): LiveData<List<Trip>> = tripDao.getAllTrips()
    
    fun getTripsByStatus(status: TripStatus): LiveData<List<Trip>> = 
        tripDao.getTripsByStatus(status)
    
    fun getTripsByDriver(driverId: String): LiveData<List<Trip>> = 
        tripDao.getTripsByDriver(driverId)
    
    fun getTripsBySupervisor(supervisorId: String): LiveData<List<Trip>> = 
        tripDao.getTripsBySupervisor(supervisorId)
    
    suspend fun getTripById(tripId: String): Trip? = 
        tripDao.getTripById(tripId)
    
    suspend fun getTripByQrCode(qrCode: String): Trip? = 
        tripDao.getTripByQrCode(qrCode)
    
    suspend fun insertTrip(trip: Trip): Long = 
        tripDao.insertTrip(trip)
    
    suspend fun updateTrip(trip: Trip) = 
        tripDao.updateTrip(trip)
    
    suspend fun deleteTrip(trip: Trip) = 
        tripDao.deleteTrip(trip)
    
    suspend fun updateTripStatus(tripId: String, status: TripStatus) = 
        tripDao.updateTripStatus(tripId, status)
    
    suspend fun getTripCountByStatus(status: TripStatus): Int = 
        tripDao.getTripCountByStatus(status)
    
    suspend fun getTripCountByDriverAndStatus(driverId: String, status: TripStatus): Int = 
        tripDao.getTripCountByDriverAndStatus(driverId, status)
}