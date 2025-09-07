package com.irancargocompany.logistics.data.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.irancargocompany.logistics.model.User
import com.irancargocompany.logistics.model.UserType

@Dao
interface UserDao {
    
    @Query("SELECT * FROM users ORDER BY createdAt DESC")
    fun getAllUsers(): LiveData<List<User>>
    
    @Query("SELECT * FROM users WHERE userType = :userType ORDER BY createdAt DESC")
    fun getUsersByType(userType: UserType): LiveData<List<User>>
    
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): User?
    
    @Query("SELECT * FROM users WHERE accessCode = :accessCode AND isActive = 1")
    suspend fun getUserByAccessCode(accessCode: String): User?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long
    
    @Update
    suspend fun updateUser(user: User)
    
    @Delete
    suspend fun deleteUser(user: User)
    
    @Query("UPDATE users SET lastLogin = :lastLogin WHERE id = :userId")
    suspend fun updateLastLogin(userId: String, lastLogin: Long)
    
    @Query("UPDATE users SET isActive = :isActive WHERE id = :userId")
    suspend fun updateUserStatus(userId: String, isActive: Boolean)
    
    @Query("SELECT COUNT(*) FROM users WHERE userType = :userType AND isActive = 1")
    suspend fun getActiveUserCountByType(userType: UserType): Int
}